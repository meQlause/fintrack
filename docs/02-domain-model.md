# 02 — Domain Model

This document defines every entity, field, enum and business rule. Names here are the exact names used in code.

## Conventions

- **Money:** `Long`, whole rupiah (IDR has no practical sub-unit). Wrapped in the value class `Money`.
  `Double`/`Float`/`BigDecimal` for money are **forbidden** (NFR-015).
- **Time:** stored as `Long` epoch milliseconds (UTC) in the database. Domain uses `java.time.Instant`.
  Display uses the device time zone (`ZoneId.systemDefault()`). Time is obtained from an injected
  `java.time.Clock` — never call `System.currentTimeMillis()` or `Instant.now()` directly (testability).
- **IDs:** `Long` auto-generated primary keys, wrapped in value classes (`WalletId`, `CategoryId`, ...)
  in the domain layer to prevent mixing them up.
- **Soft delete:** entities that may be referenced by history use `isArchived` instead of deletion.
- **Audit fields:** every table has `createdAt` and `updatedAt` (epoch millis).

## Value classes (core:model)

```kotlin
@JvmInline value class Money(val rupiah: Long)        // always >= 0 for stored amounts
@JvmInline value class WalletId(val value: Long)
@JvmInline value class CategoryId(val value: Long)
@JvmInline value class TransactionId(val value: Long)
@JvmInline value class BudgetId(val value: Long)
@JvmInline value class InboxItemId(val value: Long)
@JvmInline value class MerchantRuleId(val value: Long)
```

`Money` provides `plus`, `minus`, `compareTo`, `isZero`, and is formatted only by the UI formatter
(`core:ui` → `MoneyFormatter`), never via `toString()` in UI.

## Enums

```kotlin
enum class WalletType { CASH, BANK, EWALLET, CREDIT_CARD, OTHER }
enum class CategoryKind { EXPENSE, INCOME }
enum class TransactionType { EXPENSE, INCOME, TRANSFER }
enum class TransactionSource { MANUAL, NOTIFICATION }
enum class MoneyDirection { DEBIT, CREDIT }            // DEBIT = money out, CREDIT = money in
enum class InboxStatus { PENDING, CONFIRMED, DISMISSED, DUPLICATE }
enum class BudgetState { NORMAL, WARNING, EXCEEDED }
enum class ThemeMode { SYSTEM, LIGHT, DARK }
```

Enums are stored in the database as their `name` string (via Room TypeConverter), never as ordinal.

---

## Entities

### Wallet

| Field | Type | Rules |
|-------|------|-------|
| walletId | Long PK | auto |
| name | String | 1–30 chars, trimmed, unique among non-archived wallets (case-insensitive) |
| type | WalletType | required |
| initialBalance | Long | may be negative only for CREDIT_CARD (debt); otherwise ≥ 0 |
| iconKey | String | key from the icon catalog (`08-design-system.md`) |
| colorKey | String | key from the color catalog |
| isIncludedInTotal | Boolean | default true |
| isArchived | Boolean | default false |
| sortOrder | Int | default = max + 1 |
| createdAt / updatedAt | Long | |

**Balance is computed, never stored:**

```
balance(wallet) = initialBalance
                + Σ INCOME amount where walletId = wallet
                − Σ EXPENSE amount where walletId = wallet
                − Σ TRANSFER (amount + feeAmount) where walletId = wallet       (source)
                + Σ TRANSFER amount where targetWalletId = wallet               (target)
```

Computed in a single SQL query in `core:database` (`WalletDao.observeBalances()`), exposed as `Flow`.

### Category

| Field | Type | Rules |
|-------|------|-------|
| categoryId | Long PK | auto |
| name | String | 1–24 chars, unique per kind among non-archived |
| kind | CategoryKind | immutable after creation |
| iconKey | String | |
| colorKey | String | |
| isDefault | Boolean | true for seeded categories (they can still be edited/archived) |
| isArchived | Boolean | |
| sortOrder | Int | |
| createdAt / updatedAt | Long | |

### Transaction

| Field | Type | Rules |
|-------|------|-------|
| transactionId | Long PK | auto |
| type | TransactionType | required |
| amount | Long | 1 … 999_999_999_999 (BR-001) |
| walletId | Long FK → Wallet | the wallet money leaves (EXPENSE, TRANSFER source) or enters (INCOME) |
| targetWalletId | Long? FK → Wallet | required for TRANSFER, null otherwise (BR-002) |
| categoryId | Long? FK → Category | required for EXPENSE/INCOME, null for TRANSFER (BR-002) |
| feeAmount | Long | ≥ 0, TRANSFER only, default 0 |
| note | String | 0–120 chars, default "" |
| merchant | String | 0–60 chars, default "" (filled from notification or user) |
| occurredAt | Long | when the transaction happened (user-editable) |
| source | TransactionSource | MANUAL or NOTIFICATION |
| inboxItemId | Long? FK → InboxItem | set when created from inbox |
| createdAt / updatedAt | Long | |

Indexes: `(occurredAt)`, `(walletId, occurredAt)`, `(targetWalletId)`, `(categoryId, occurredAt)`.

### Budget

| Field | Type | Rules |
|-------|------|-------|
| budgetId | Long PK | auto |
| categoryId | Long? FK → Category | null = overall budget (FR-074); unique |
| limitAmount | Long | > 0 |
| isActive | Boolean | default true |
| createdAt / updatedAt | Long | |

A budget applies to every period until changed. Spent amount is computed from transactions in the current period.

### BudgetAlertLog

Prevents repeated alerts (FR-073).

| Field | Type | Rules |
|-------|------|-------|
| budgetId | Long FK | part of composite PK |
| periodStart | Long | part of composite PK (epoch millis of period start) |
| threshold | Int | 80 or 100; part of composite PK |
| notifiedAt | Long | |

### InboxItem (detected from notification)

| Field | Type | Rules |
|-------|------|-------|
| inboxItemId | Long PK | auto |
| packageName | String | source app package |
| parserId | String | e.g. `bca.debit.v1` |
| rawTitle | String | purged (set to "") 30 days after resolution (FR-092) |
| rawText | String | purged as above |
| postedAt | Long | notification post time |
| amount | Long | parsed |
| direction | MoneyDirection | parsed |
| merchant | String | parsed, may be "" |
| suggestedWalletId | Long? | from monitored app default wallet |
| suggestedCategoryId | Long? | from merchant rule |
| status | InboxStatus | PENDING on insert |
| fingerprint | String | unique; dedup key (see 07) |
| relatedInboxItemId | Long? | possible pair (FR-091) |
| transactionId | Long? | set on CONFIRMED |
| resolvedAt | Long? | set on CONFIRMED/DISMISSED |
| createdAt / updatedAt | Long | |

### MerchantRule (merchant memory, FR-088)

| Field | Type | Rules |
|-------|------|-------|
| merchantRuleId | Long PK | auto |
| merchantKey | String | unique, normalized: lowercase, trimmed, collapsed spaces, digits removed, max 40 chars |
| categoryId | Long FK | |
| walletId | Long? FK | optional |
| hitCount | Int | incremented on each confirmation |
| createdAt / updatedAt | Long | |

### MonitoredApp

| Field | Type | Rules |
|-------|------|-------|
| packageName | String PK | |
| displayName | String | |
| isEnabled | Boolean | |
| defaultWalletId | Long? FK | |
| lastNotificationAt | Long? | for diagnostics (FR-090) |

### Settings (DataStore, not Room)

| Key | Type | Default |
|-----|------|---------|
| onboardingCompleted | Boolean | false |
| themeMode | ThemeMode | SYSTEM |
| periodStartDay | Int (1–28) | 1 |
| hideAmountsByDefault | Boolean | false |
| lastUsedWalletId | Long? | null |
| lastUsedTransactionType | TransactionType | EXPENSE |
| pinHash / pinSalt | String? | null (see 10) |
| biometricEnabled | Boolean | false |
| lockTimeoutSeconds | Int | 30 |
| failedPinAttempts / lockedUntil | Int / Long | 0 / 0 |
| hideFromRecents | Boolean | false |
| lastBackupAt | Long? | null |
| backupReminderDays | Int (0 = off) | 7 |
| headsUpForInbox | Boolean | true |

---

## Business rules

| ID | Rule |
|----|------|
| BR-001 | Transaction amount: 1 ≤ amount ≤ 999,999,999,999 rupiah. |
| BR-002 | EXPENSE/INCOME require `categoryId` and no `targetWalletId`. TRANSFER requires `targetWalletId` and no `categoryId`. |
| BR-003 | TRANSFER: `walletId != targetWalletId`. |
| BR-004 | TRANSFER moves `amount` from source to target; `feeAmount` is additionally deducted from the source. Transfers never count as income or expense in statistics. |
| BR-005 | Transfer fees **do** count as expense in statistics under the virtual category "Biaya Transfer". |
| BR-006 | Category kind must match transaction type (EXPENSE ↔ EXPENSE category, INCOME ↔ INCOME category). |
| BR-007 | Archived wallets/categories cannot be selected for new transactions but remain valid for existing ones. |
| BR-008 | A wallet or category with references cannot be deleted, only archived. |
| BR-009 | `occurredAt` may be in the past (any) or up to 1 day in the future (time-zone tolerance). |
| BR-010 | Period: given start day `S` and date `D`: if `D.day >= S`, period starts on `S` of D's month; else on `S` of the previous month. Ends the day before the next start. |
| BR-011 | Confirming an inbox item creates exactly one transaction and sets item status CONFIRMED atomically (single DB transaction). |
| BR-012 | Confirming an inbox item upserts the MerchantRule for its normalized merchant (when merchant ≠ ""). |
| BR-013 | DEBIT inbox item → EXPENSE suggestion; CREDIT → INCOME suggestion; paired items (FR-091) → TRANSFER suggestion. |
| BR-014 | Total balance = Σ balance of wallets where `isIncludedInTotal && !isArchived`. |
| BR-020 | Budget state: ratio = spent / limit. `< 0.80` NORMAL, `0.80 ≤ ratio < 1.00` WARNING, `≥ 1.00` EXCEEDED. Use integer math: `spent * 100 / limit`. |
| BR-021 | Budget alert for a threshold fires at most once per budget per period (BudgetAlertLog). |

## Default categories (seeded, FR-004)

EXPENSE:

| name (UI, Indonesian) | iconKey | colorKey |
|-----------------------|---------|----------|
| Makanan & Minuman | food | orange |
| Transportasi | transport | blue |
| Belanja | shopping | pink |
| Tagihan | bill | purple |
| Hiburan | entertainment | violet |
| Kesehatan | health | red |
| Pendidikan | education | indigo |
| Rumah | home | brown |
| Pulsa & Internet | phone | cyan |
| Sosial & Kondangan | gift | rose |
| Donasi & Zakat | charity | green |
| Lainnya | other | gray |

INCOME:

| name | iconKey | colorKey |
|------|---------|----------|
| Gaji | salary | green |
| Bonus | bonus | teal |
| Usaha / Freelance | business | blue |
| Hadiah | gift | rose |
| Lainnya | other | gray |

Seeded names come from string resources at first launch, then stored as plain data (renaming later is user data).

## Entity relationships

```
Wallet 1 ──< Transaction (walletId)
Wallet 1 ──< Transaction (targetWalletId, TRANSFER only)
Category 1 ──< Transaction (categoryId, EXPENSE/INCOME only)
Category 1 ──1 Budget (categoryId, nullable = overall)
Budget 1 ──< BudgetAlertLog
InboxItem 1 ──0..1 Transaction
InboxItem 0..1 ── InboxItem (relatedInboxItemId)
Category 1 ──< MerchantRule
Wallet 1 ──< MerchantRule (optional)
Wallet 1 ──< MonitoredApp (defaultWalletId, optional)
```

Foreign keys use `onDelete = RESTRICT` (deletion is guarded by BR-008), except `BudgetAlertLog → Budget` which is `CASCADE`.

## Database

- Name: `fintrack.db`, Room, encrypted with SQLCipher.
- Schema version starts at 1. Room `exportSchema = true`; schemas committed under `core/database/schemas/`.
- Every schema change: new version + `Migration` + migration test (NFR-009).
