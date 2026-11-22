# 15 — Glossary

## Indonesian UI term ↔ English code term

| Indonesian (UI) | English (code) | Meaning |
|-----------------|----------------|---------|
| Beranda | Dashboard | Home screen |
| Riwayat | History | Transaction list |
| Statistik | Statistics | Charts and breakdowns |
| Dompet | Wallet | A money source: cash, bank account, e-wallet, credit card |
| Tunai | Cash | Wallet type CASH |
| Bank | Bank | Wallet type BANK |
| E-Wallet / Dompet digital | E-wallet | Wallet type EWALLET (GoPay, OVO, DANA, ShopeePay) |
| Kartu Kredit | Credit card | Wallet type CREDIT_CARD |
| Kategori | Category | Classification of income/expense |
| Transaksi | Transaction | One money movement |
| Pengeluaran | Expense | Money out (TransactionType.EXPENSE) |
| Pemasukan | Income | Money in (TransactionType.INCOME) |
| Transfer / Pindah dana | Transfer | Money between own wallets (TransactionType.TRANSFER) |
| Biaya admin | Fee (`feeAmount`) | Transfer fee |
| Saldo | Balance | Current amount in a wallet |
| Saldo | Balance (liquid) | Sum of non-credit included wallets (BR-043) |
| Saldo awal | Initial balance | Wallet balance when created |
| Selisih | Net | Income − expense |
| Anggaran | Budget | Monthly spending limit |
| Periode | Period | Month starting on the configured start day (BR-010) |
| Awal periode | Period start day | 1–28 |
| Kotak Masuk | Inbox | Detected, unconfirmed transactions |
| Catat | Confirm / record | Turn an inbox item into a transaction |
| Abaikan | Dismiss | Reject an inbox item |
| Otomatis / Terdeteksi | Detected (source NOTIFICATION) | Created from a notification |
| Aplikasi dipantau | Monitored app | Bank/e-wallet app whose notifications are read |
| Cadangan | Backup | Encrypted backup file |
| Pulihkan | Restore | Load a backup |
| Ekspor | Export | CSV export |
| Kata sandi | Password | Backup password |
| PIN | PIN | 6-digit app lock code |
| Catatan | Note | Free-text note on a transaction |
| Merchant / Toko | Merchant | Where the money went/came from |
| Diarsipkan | Archived | Hidden but kept (`isArchived`) |
| Paylater | Pay-later | Wallet type PAYLATER (SPayLater, GoPayLater, Kredivo, …) |
| Limit | Credit limit | `creditLimit` of a credit wallet |
| Sisa limit | Available limit | BR-044 |
| Tagihan | Statement / bill | Amount due for a statement cycle (BR-033) |
| Tanggal cetak | Statement day | `statementDay` |
| Jatuh tempo | Due date | `dueDay` / statement due date |
| Lunas | Paid / settled | StatementStatus.PAID, LoanStatus.SETTLED |
| Terlambat | Overdue | StatementStatus.OVERDUE |
| Cicilan | Instalment plan | InstallmentPlan |
| Tenor | Tenor | `tenorMonths` |
| Biaya admin | Admin fee | One-off instalment fee (BR-038) |
| Utang kartu | Card debt | Σ outstanding of credit wallets |
| Piutang | Receivable | Loan with direction LENT |
| Hutang | Debt | Loan with direction BORROWED |
| Pinjamkan / Pinjam | Lend / Borrow | Transaction types LEND / BORROW |
| Catat pembayaran | Record repayment | COLLECT (for piutang) / REPAY (for hutang) |
| Ikhlaskan | Write off | LoanStatus.WRITTEN_OFF (BR-042) |
| Kekayaan bersih | Net worth | BR-043 |

## Technical terms

| Term | Meaning |
|------|---------|
| Fingerprint | Hash identifying a notification-derived item, used for dedup (07) |
| Merchant rule | Remembered merchant → category/wallet mapping (FR-088) |
| Merchant key | Normalized merchant string used to look up merchant rules |
| Parser | Code that converts one app's notification text into a ParsedTransaction |
| Fixture | A stored real (anonymized) notification sample with its expected parse result |
| Pair / paired items | A debit in one app and a credit in another that are likely one transfer (FR-091) |
| Route (composable) | Stateful screen entry that owns the ViewModel |
| Screen (composable) | Stateless UI receiving state + callbacks |
| UiState | Immutable data describing everything a screen shows |
| Use case | One business operation in `core:domain` |
| Fake | Hand-written in-memory test double implementing a real interface |
| Convention plugin | Shared Gradle configuration in `build-logic` |
| Gate | `./gradlew check` — must pass for any change |
| QRIS | Indonesian national QR payment standard (appears in bank/e-wallet notifications) |
