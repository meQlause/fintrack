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
@JvmInline value class InstallmentPlanId(val value: Long)
@JvmInline value class LoanId(val value: Long)
```

`Money` provides `plus`, `minus`, `compareTo`, `isZero`, and is formatted only by the UI formatter
(`core:ui` → `MoneyFormatter`), never via `toString()` in UI.

## Enums

```kotlin
enum class WalletType { CASH, BANK, EWALLET, CREDIT_CARD, PAYLATER, INVESTMENT, OTHER }   // CREDIT_CARD, PAYLATER = "credit wallets"
enum class InvestKind { MUTUAL_FUND, STOCK, GOLD, DEPOSIT, CRYPTO, OTHER }
enum class CategoryKind { EXPENSE, INCOME }
enum class TransactionType { EXPENSE, INCOME, TRANSFER, LEND, BORROW, COLLECT, REPAY }
enum class InstallmentStatus { ACTIVE, PAID_OFF }
enum class LoanDirection { LENT, BORROWED }                       // LENT = piutang, BORROWED = hutang
enum class LoanStatus { OPEN, SETTLED, WRITTEN_OFF }
enum class StatementStatus { NOT_ISSUED, DUE, PAID, OVERDUE }      // computed, never stored
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
| initialBalance | Long | credit wallets: ≤ 0 (= −current outstanding at creation); others: ≥ 0 |
| creditLimit | Long? | credit wallets only, > 0; null otherwise |
| statementDay | Int? | credit wallets only, 1–28 |
| dueDay | Int? | credit wallets only, 1–28 |
| investKind | InvestKind? | INVESTMENT wallets only |
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
                − Σ LEND and REPAY amount where walletId = wallet               (money out to a person)
                + Σ BORROW and COLLECT amount where walletId = wallet           (money in from a person)
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
| loanId | Long? FK → Loan | required for LEND/BORROW/COLLECT/REPAY, null otherwise |
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

### InstallmentPlan (FR-140 … FR-146)

| Field | Type | Rules |
|-------|------|-------|
| installmentPlanId | Long PK | auto |
| transactionId | Long FK → Transaction | unique; the EXPENSE purchase (on a credit wallet) |
| walletId | Long FK → Wallet | must be a credit wallet; equals the purchase's walletId |
| title | String | 1–40 chars, defaults to purchase note/merchant |
| principal | Long | = purchase amount |
| tenorMonths | Int | 2–36 |
| firstStatementMonth | String | `YYYY-MM` of the statement that bills portion 1 |
| adminFeeTransactionId | Long? FK → Transaction | EXPENSE in category "Biaya & Admin", if a fee was entered |
| status | InstallmentStatus | ACTIVE / PAID_OFF |
| earlyPayoffMonth | String? | `YYYY-MM` statement that bills all remaining portions (BR-037) |
| createdAt / updatedAt | Long | |

Portion amounts and schedule are **computed** (BR-035, BR-036), never stored.

### Loan (FR-150 … FR-157)

| Field | Type | Rules |
|-------|------|-------|
| loanId | Long PK | auto |
| direction | LoanDirection | LENT or BORROWED, immutable |
| personName | String | 1–40 chars, trimmed |
| principal | Long | = amount of the opening LEND/BORROW transaction |
| startDate | Long | = opening transaction occurredAt |
| dueDate | Long? | optional |
| note | String | 0–120 chars |
| status | LoanStatus | OPEN / SETTLED / WRITTEN_OFF |
| closedAt | Long? | set when SETTLED or WRITTEN_OFF |
| createdAt / updatedAt | Long | |

The opening transaction (LEND or BORROW) and all repayments (COLLECT or REPAY) are Transaction rows with this `loanId`.

### ValuationEntry (FR-172)

| Field | Type | Rules |
|-------|------|-------|
| valuationId | Long PK | auto |
| walletId | Long FK → Wallet | INVESTMENT wallets only, CASCADE on delete |
| value | Long | ≥ 0 |
| valuedAt | Long | defaults to now |
| createdAt | Long | |

### QuickEntry — shortcut / pintasan (FR-039)

| Field | Type | Rules |
|-------|------|-------|
| quickEntryId | Long PK | auto |
| title | String | 1–24 chars; becomes the transaction note when applied |
| type | TransactionType | EXPENSE or INCOME only |
| amount | Long | 1 … 999_999_999_999 (BR-001) |
| categoryId | Long FK → Category | kind must match type (BR-006) |
| walletId | Long FK → Wallet | INCOME: non-credit wallets only |
| sortOrder | Int | |
| createdAt / updatedAt | Long | |

Applying a QuickEntry only fills a draft (BR-047). Archived categories/wallets referenced by a QuickEntry fall back to the defaults when applied.

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
| BR-002 | EXPENSE/INCOME require `categoryId` and no `targetWalletId`. TRANSFER requires `targetWalletId` and no `categoryId`. LEND/BORROW/COLLECT/REPAY require `loanId` and have neither `categoryId` nor `targetWalletId`. |
| BR-003 | TRANSFER: `walletId != targetWalletId`. |
| BR-004 | TRANSFER moves `amount` from source to target; `feeAmount` is additionally deducted from the source. Transfers never count as income or expense in statistics. |
| BR-005 | Transfer fees **do** count as expense in statistics under the default category "Biaya & Admin". |
| BR-006 | Category kind must match transaction type (EXPENSE ↔ EXPENSE category, INCOME ↔ INCOME category). |
| BR-007 | Archived wallets/categories cannot be selected for new transactions but remain valid for existing ones. |
| BR-008 | A wallet or category with references cannot be deleted, only archived. |
| BR-009 | `occurredAt` may be in the past (any) or up to 1 day in the future (time-zone tolerance). |
| BR-010 | Period: given start day `S` and date `D`: if `D.day >= S`, period starts on `S` of D's month; else on `S` of the previous month. Ends the day before the next start. |
| BR-011 | Confirming an inbox item creates exactly one transaction and sets item status CONFIRMED atomically (single DB transaction). |
| BR-012 | Confirming an inbox item upserts the MerchantRule for its normalized merchant (when merchant ≠ ""). |
| BR-013 | DEBIT inbox item → EXPENSE suggestion; CREDIT → INCOME suggestion; paired items (FR-091) → TRANSFER suggestion. |
| BR-014 | ~~Total balance = Σ balance of wallets where `isIncludedInTotal && !isArchived`.~~ Superseded by BR-043. |
| BR-020 | Budget state: ratio = spent / limit. `< 0.80` NORMAL, `0.80 ≤ ratio < 1.00` WARNING, `≥ 1.00` EXCEEDED. Use integer math: `spent * 100 / limit`. |
| BR-021 | Budget alert for a threshold fires at most once per budget per period (BudgetAlertLog). |
| BR-030 | Spending is recognised at purchase (ADR-015). An instalment purchase is one EXPENSE of the full principal in the purchase period. |
| BR-031 | Credit wallet: `outstanding = max(0, −balance)`. A positive balance is an overpayment and is allowed. |
| BR-032 | Statement *S* for month `YYYY-MM`: cut-off = `statementDay` of that month (end of day); covers the day after the previous cut-off up to this cut-off. Due date = `dueDay` of the same month if `dueDay > statementDay`, otherwise of the next month. |
| BR-033 | `statementAmount(S) = outstanding at cut-off(S) − unbilled instalment principal at cut-off(S)`. `paidSinceCutoff(S) = Σ TRANSFER into the wallet after cut-off(S)`. `remainingDue(S) = max(0, statementAmount(S) − paidSinceCutoff(S))`. Only the latest issued statement is "current". |
| BR-034 | Statement status: before the first cut-off after wallet creation → NOT_ISSUED; remainingDue = 0 → PAID; today ≤ due date → DUE; else OVERDUE. |
| BR-035 | Instalment portions: `base = principal / tenor` (integer division); portions 1…tenor−1 = base; last = principal − base × (tenor − 1). |
| BR-036 | Portion *k* is billed on statement `firstStatementMonth + (k − 1)` months. Default `firstStatementMonth` = first statement whose cut-off ≥ purchase date. Unbilled principal at a cut-off = Σ portions scheduled on later statements, **counting only plans whose purchase date ≤ that cut-off** (a purchase after the cut-off is not part of that statement at all). |
| BR-037 | Early payoff sets `earlyPayoffMonth` = next statement; all unbilled portions are billed there; status → PAID_OFF after that statement is PAID. |
| BR-038 | An instalment admin fee is a separate EXPENSE on the same credit wallet, category "Biaya & Admin", dated with the purchase. |
| BR-039 | Paying a credit wallet is always a TRANSFER from a non-credit wallet; it is never an EXPENSE. Transfers between two credit wallets are rejected. |
| BR-040 | LEND, BORROW, COLLECT and REPAY change wallet balances but are excluded from income/expense statistics and budgets. |
| BR-041 | Loan `outstanding = principal − Σ repayments` (COLLECT for LENT, REPAY for BORROWED). A repayment > outstanding is rejected. Outstanding 0 → status SETTLED, `closedAt` = now. |
| BR-042 | Write-off (LENT only, OPEN only): status → WRITTEN_OFF; no transaction is created; statistics unchanged. |
| BR-043 | Saldo = Σ balance of non-credit wallets with `isIncludedInTotal && !isArchived`. Utang kartu = Σ outstanding of non-archived credit wallets. Piutang / Hutang = Σ outstanding of OPEN LENT / BORROWED loans. Kekayaan bersih = Saldo + Piutang − Utang kartu − Hutang. |
| BR-044 | Available limit = `creditLimit − outstanding` (may be negative if over limit; shown as "Melebihi limit"). |
| BR-045 | Reminders fire at 09:00 local on D−3 and D−1 before a due date, only while remainingDue > 0 (statements) or outstanding > 0 (loans); at most once per item per day. |
| BR-046 | Deleting an opening LEND/BORROW transaction deletes the loan and its repayments after confirmation. Deleting a repayment reopens a SETTLED loan. |
| BR-050 | INVESTMENT wallets take part only in TRANSFERs (in or out). They cannot be the wallet of EXPENSE, INCOME, LEND, BORROW, COLLECT or REPAY, nor have instalment plans. |
| BR-051 | `modal = initialBalance + Σ transfers in − Σ transfers out`. `currentValue = lastValuation.value + Σ transfers in after it − Σ transfers out after it` ("after" compares full timestamps, so a same-day withdrawal after an update counts); with no valuation, `currentValue = modal`. `gain = currentValue − modal`; `gain% = gain / modal` (integer math, one decimal in UI). |
| BR-052 | Investment gains/losses are shown only on investment screens and never counted as income/expense. |
| BR-053 | Saldo (BR-043) excludes INVESTMENT wallets. Kekayaan bersih = Saldo + Investasi (Σ currentValue) + Piutang − Utang kartu − Hutang. |
| BR-054 | A valuation dated today replaces another valuation of the same wallet on the same day. |
| BR-055 | Optional monthly reminder on day 1 at 09:00 when at least one INVESTMENT wallet exists (setting, default off). |
| BR-047 | A QuickEntry never creates a transaction by itself: it pre-fills the add sheet, the user may edit every field, and only "Simpan" persists the transaction. |

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
| Rumah | home | amber |
| Pulsa & Internet | phone | cyan |
| Sosial & Kondangan | gift | rose |
| Donasi & Zakat | charity | green |
| Biaya & Admin | fee | slate |
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
Transaction 1 ──0..1 InstallmentPlan (transactionId = the purchase)
Wallet 1 ──< InstallmentPlan (credit wallets only)
Loan 1 ──< Transaction (loanId: opening LEND/BORROW + COLLECT/REPAY repayments)
```

Foreign keys use `onDelete = RESTRICT` (deletion is guarded by BR-008), except `BudgetAlertLog → Budget` and `ValuationEntry → Wallet`, which are `CASCADE`.

## Database

- Name: `fintrack.db`, Room, encrypted with SQLCipher.
- Schema version starts at 1. Room `exportSchema = true`; schemas committed under `core/database/schemas/`.
- Every schema change: new version + `Migration` + migration test (NFR-009).
