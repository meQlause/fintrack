# 07 — Notification Detection

Reads notifications from bank and e-wallet apps **on-device**, extracts transaction data, and puts it in the
**Inbox** for the user to confirm. Nothing is ever sent anywhere (no internet permission).

Requirements: FR-080 … FR-092. Modules: `core:notification` (Android service), `core:parser` (pure Kotlin).

## Pipeline

```
Android posts notification
  │
  ▼
FintrackNotificationListener.onNotificationPosted(sbn)       [core:notification]
  │ 1. Is sbn.packageName a MonitoredApp with isEnabled? ── no ──► return (nothing stored)
  │ 2. Skip group summaries (FLAG_GROUP_SUMMARY) and ongoing notifications
  │ 3. Extract RawNotification(packageName, title, text, bigText, postedAt, notificationKey)
  │ 4. Launch on application scope (DefaultDispatcher) ── never block the callback
  ▼
NotificationParserRegistry.parse(raw)                          [core:parser]
  │ picks parsers registered for packageName, tries in order
  │ returns ParseResult: Transaction(parsed) | Ignored(reason) | Unrecognized
  ▼
  ├─ Ignored / Unrecognized ──► discard. Only increment a counter (no text stored). (FR-085)
  ▼
ProcessDetectedTransactionUseCase(parsed)                      [core:domain]
  │ 1. compute fingerprint; if exists ──► discard (repost)              (FR-086)
  │ 2. look up MerchantRule by normalized merchant ──► suggested category/wallet (FR-088)
  │ 3. suggested wallet fallback: MonitoredApp.defaultWalletId              (FR-082)
  │ 4. look for a pair candidate (opposite direction, same amount, ≤10 min, other app) (FR-091)
  │ 5. insert InboxItem(status = PENDING)
  │ 6. update MonitoredApp.lastNotificationAt
  ▼
InboxNotifier.showConfirmPrompt(item)  (if headsUpForInbox)    [core:notification]
  │ "Rp25.000 di INDOMARET — Konfirmasi?"  [Konfirmasi] [Abaikan]
  ▼
User confirms (inbox screen, swipe, or notification action)
  ▼
ConfirmInboxItemUseCase  ──► creates Transaction(source = NOTIFICATION) + sets CONFIRMED + upserts MerchantRule (BR-011, BR-012)
```

## Data types (core:model; `ParseResult` stays in core:parser)

```kotlin
data class RawNotification(
    val packageName: String,
    val title: String,
    val text: String,          // EXTRA_TEXT
    val bigText: String,       // EXTRA_BIG_TEXT, "" if absent
    val postedAt: Instant,
    val notificationKey: String,
)

data class ParsedTransaction(
    val amount: Money,
    val direction: MoneyDirection,
    val merchant: String,      // "" if unknown
    val occurredAt: Instant,   // = postedAt unless the text contains a time
    val parserId: String,
)

sealed interface ParseResult {
    data class Transaction(val parsed: ParsedTransaction) : ParseResult
    data class Ignored(val reason: IgnoreReason) : ParseResult
    data object Unrecognized : ParseResult
}

enum class IgnoreReason { OTP, PROMOTION, BALANCE_INFO, FAILED_TRANSACTION, PENDING_TRANSACTION, LOGIN_ALERT }

interface NotificationParser {
    val parserId: String                       // e.g. "bca.v1"
    val packageNames: Set<String>
    fun parse(raw: RawNotification): ParseResult
}
```

The text used for matching is `bigText.ifEmpty { text }`, combined with `title` when a parser needs it.

## Parsing rules (shared utilities in core:parser)

### Amount parsing — `IndonesianAmountParser`

Must handle all of these (each is a test case):

| Input fragment | Result |
|----------------|--------|
| `Rp25.000` | 25000 |
| `Rp 25.000` | 25000 |
| `Rp25.000,00` | 25000 |
| `Rp 1.250.000,50` | 1250000 (sub-rupiah truncated, BR-001 whole rupiah) |
| `IDR 25,000.00` | 25000 (English format) |
| `IDR25000` | 25000 |
| `Rp. 15.500` | 15500 |
| `Rp15rb` | **not supported** → Unrecognized (banks never send this) |
| `Rp0` | Ignored (zero) |

Algorithm: find `(Rp\.?|IDR)\s?` followed by `[0-9.,]+`. Decide the decimal separator: if the last separator is
followed by exactly 2 digits **and** the other separator type appears before it, treat it as the decimal part;
if only one separator type appears and groups after it are 3 digits, it is a thousands separator.
Never use `toDouble()`. Parse digits into `Long`.

When a text contains several amounts (e.g. amount + fee + balance), the **parser for that app** decides which
one is the transaction amount, using its template. Never take "the first amount" blindly.

### Direction keywords (Indonesian & English, case-insensitive)

| DEBIT (money out) | CREDIT (money in) |
|-------------------|-------------------|
| pembayaran, bayar, membayar, berhasil dibayar | menerima, diterima, terima |
| transfer ke, kirim ke, mengirim | transfer dari, kiriman dari, dana masuk |
| tarik tunai, penarikan | top up berhasil (e-wallet side), isi saldo |
| debit, didebet, pendebetan | kredit, dikreditkan |
| pembelian, belanja | refund, pengembalian dana, cashback diterima |

### Ignore keywords → `Ignored`

| Reason | Keywords |
|--------|----------|
| OTP | otp, kode verifikasi, kode rahasia, jangan berikan, one time password |
| PROMOTION | promo, diskon, voucher, cashback hingga, s.d., dapatkan, gratis ongkir, hemat |
| BALANCE_INFO | saldo anda saat ini, info saldo, sisa saldo (when no transaction verb present) |
| FAILED_TRANSACTION | gagal, ditolak, dibatalkan, tidak berhasil |
| PENDING_TRANSACTION | sedang diproses, menunggu pembayaran, belum dibayar |
| LOGIN_ALERT | login, masuk ke akun, perangkat baru |

Ignore checks run **before** transaction matching. OTP check runs first of all.

### Merchant extraction

- Taken from the app-specific template (e.g. text after `ke ` / `di ` up to ` berhasil`, `.`, or end).
- Trimmed, max 60 chars, uppercase preserved as-is for display.
- `MerchantNormalizer` creates `merchantKey` for MerchantRule: lowercase, remove digits and punctuation,
  collapse whitespace, remove common suffixes (`cabang`, `tbk`, `pt`, `cv`), max 40 chars.
  Example: `INDOMARET CABANG KEMANG 12` → `indomaret kemang`.

## Fingerprint (dedup, FR-086)

```
fingerprint = sha256( packageName | amount | direction | normalizedText | floor(postedAt / 60s) )
```

`normalizedText` = matched text with digits of reference numbers removed and whitespace collapsed.
This makes Android re-posting/updating the same notification produce the same fingerprint.
`InboxItem.fingerprint` has a UNIQUE index; insert uses `OnConflictStrategy.IGNORE`.

## Pairing (FR-091)

Candidate pair when all hold:
- other item is PENDING, from a **different** package,
- opposite direction, identical amount,
- `|postedAt difference| ≤ 10 minutes`.

Then both items get `relatedInboxItemId` pointing at each other, and the Inbox shows them as one card:
"Sepertinya transfer dari BCA ke GoPay — Rp100.000" with action "Catat sebagai Transfer". Confirming creates
one TRANSFER and marks both items CONFIRMED.

## Supported apps — initial list

Package names **must be verified on a real device** before release (Q-004). Parsers are added one by one,
each with fixtures from real notifications provided by the owner/friends.

| App | Expected package (verify) | Wallet type | Priority |
|-----|---------------------------|-------------|----------|
| myBCA | `com.bca.mybca.omni.android` | BANK | P2 |
| BCA mobile | `com.bca` | BANK | P2 |
| Livin' by Mandiri | `id.bmri.livin` | BANK | P2 |
| BRImo | `id.co.bri.brimo` | BANK | P2 |
| wondr by BNI / BNI Mobile | verify | BANK | P2 |
| Jago | verify | BANK | later |
| SeaBank | verify | BANK | later |
| GoPay / Gojek | `com.gojek.gopay`, `com.gojek.app` | EWALLET | P2 |
| OVO | `ovo.id` | EWALLET | P2 |
| DANA | `id.dana` | EWALLET | P2 |
| ShopeePay (Shopee) | `com.shopee.id` | EWALLET | P2 |

**First parsers (Q-003): BCA (myBCA + BCA mobile), GoPay, DANA.** The others follow later in P5.

Because the repository is public (ADR-013), fixtures must be fully anonymized before committing:
names, account/card numbers, reference/transaction IDs and phone numbers → `XXXX`.

## Parser implementation template

```
core/parser/src/main/kotlin/com/meqlause/fintrack/core/parser/
├── NotificationParser.kt          // interface
├── NotificationParserRegistry.kt  // map packageName -> parsers
├── model/                         // RawNotification, ParsedTransaction, ParseResult, IgnoreReason
├── common/
│   ├── IndonesianAmountParser.kt
│   ├── IgnoreClassifier.kt
│   ├── DirectionClassifier.kt
│   └── MerchantNormalizer.kt
├── bank/
│   ├── BcaParser.kt
│   └── MandiriParser.kt
└── ewallet/
    ├── GopayParser.kt
    └── DanaParser.kt
```

Each parser:
1. Run `IgnoreClassifier` → return `Ignored` if matched.
2. Try its templates (ordered list of `Regex` with named groups `amount`, `merchant`).
3. On match → build `ParsedTransaction` with `parserId = "<app>.<template>.v<n>"`.
4. No template matched → `Unrecognized`.
5. A parser must never throw. Any unexpected input → `Unrecognized`.

Steps to add a new app:
1. Collect ≥ 3 real notifications per type (debit, credit, ignored) → anonymize → fixtures.
2. Write the parameterized fixture tests (they fail).
3. Implement the parser, register it in `NotificationParserRegistry`.
4. Add the app to the supported list above and to `SupportedApps` constant.
5. `./gradlew :core:parser:check`.

## Android service details (core:notification)

- `FintrackNotificationListener : NotificationListenerService`, declared with
  `android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"` and the intent filter
  `android.service.notification.NotificationListenerService`.
- Hilt `@AndroidEntryPoint`.
- Access check: `NotificationManagerCompat.getEnabledListenerPackages(context).contains(packageName)`.
- Open settings: `Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS`.
- `onListenerDisconnected` → `requestRebind(ComponentName)`.
- OEM battery killers (Xiaomi, Oppo, Vivo, Samsung): onboarding/settings shows a help page explaining how to
  disable battery optimization (`ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS`) and allow autostart.
- Diagnostics (FR-090): Settings shows "Akses notifikasi: Aktif/Tidak aktif" and per app "Terakhir diterima: ...".
- Our own notifications use channels: `inbox_prompt` (high importance), `budget_alert` (default), `backup_reminder` (low).
- Requires `POST_NOTIFICATIONS` runtime permission on Android 13+ for our own prompts.

## Credit cards & pay-later (FR-165)

- `ParsedTransaction` gains `instrumentHint: InstrumentHint` (`ACCOUNT`, `CARD`, `PAYLATER`) and `cardLast4: String`
  ("" if absent). Parsers set it from keywords: "kartu kredit", "credit card", "CC" → CARD; "PayLater", "Paylater", "Kredivo" → PAYLATER.
- Wallet mapping: `(packageName, instrumentHint, cardLast4)` → wallet, configured per monitored app in S-13
  ("Kartu ••1234 → BCA Card"). Falls back to the app's default wallet.
- A debit from a bank account + a payment-received notification on a card/pay-later of the same amount within 10 minutes
  is suggested as a bill-payment TRANSFER (extends FR-091).
- Fixtures must mask card numbers except the last 4 digits; the last 4 are replaced by `0000` in committed fixtures (ADR-013).

## Privacy guarantees

- Only notifications from **enabled monitored apps** are even read beyond `packageName`.
- Non-transaction notifications are discarded in memory; their text is never persisted or logged.
- Raw text of resolved inbox items is purged after 30 days by a daily WorkManager job (FR-092).
- `AppLogger` never logs notification text or amounts.
