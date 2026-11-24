# 01 — Requirements

Each requirement has a stable ID. Priority: **M** = must (v1), **S** = should (v1 if time allows), **C** = could (v2).
Each requirement lists acceptance criteria (AC). A requirement is "done" only when every AC has a passing test
or, for purely visual criteria, a screenshot test (see `06-testing.md`).

---

## Functional requirements

### Onboarding

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-001 | M | On first launch, show a short onboarding (max 4 pages) explaining: offline/private, wallets, notification detection, security. |
| FR-002 | M | Onboarding asks the user to create at least one wallet (default suggestion: "Tunai" / Cash, balance 0). |
| FR-003 | M | Onboarding offers (skippable) notification-access setup and PIN setup. |
| FR-004 | M | Default categories are seeded on first launch (see `02-domain-model.md` → Default categories). |

AC FR-001..004:
- Onboarding is shown only when no wallet exists and the `onboardingCompleted` flag is false.
- Skipping notification access or PIN still completes onboarding.
- After onboarding, the Dashboard is shown.

### Wallets

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-010 | M | Create a wallet with: name, type, initial balance, icon, color. |
| FR-011 | M | Edit wallet name, type, icon, color. Editing the initial balance is allowed and recalculates the balance. |
| FR-012 | M | Archive a wallet (hidden from pickers, kept in history). Archived wallets can be restored. |
| FR-013 | M | Delete a wallet **only if it has no transactions**; otherwise offer archive. |
| FR-014 | M | Show each wallet's current balance and the total balance of all non-archived wallets. |
| FR-015 | S | Reorder wallets by drag and drop. |
| FR-016 | S | Exclude a wallet from the total balance (e.g. a savings wallet). |
| FR-017 | M | Tapping a wallet (Home or Dompet tab) opens a **wallet detail screen** (S-19): balance, this period's in/out/count, daily outflow bars, quick actions (Catat with this wallet, Transfer from it, Riwayat filtered), recent transactions. Credit wallets open S-14 instead. |

### Categories

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-020 | M | Categories are either EXPENSE or INCOME kind. |
| FR-021 | M | Create, edit (name, icon, color), archive categories. |
| FR-022 | M | Delete a category only if no transaction or budget uses it; otherwise offer archive. |
| FR-023 | S | Reorder categories. |

### Transactions

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-030 | M | Add an EXPENSE: amount, category, wallet, date-time, optional note. |
| FR-031 | M | Add an INCOME: amount, category, wallet, date-time, optional note. |
| FR-032 | M | Add a TRANSFER: amount, source wallet, target wallet, date-time, optional note, optional fee. |
| FR-033 | M | The add sheet opens with a numeric keypad focused; the last-used wallet and type are preselected. |
| FR-034 | M | Edit any field of an existing transaction. |
| FR-035 | M | Delete a transaction with an "Urungkan" snackbar (3 seconds, with a countdown line). |
| FR-036 | M | Transaction history grouped by day, newest first, with daily totals. |
| FR-037 | M | Filter history by: month, type, wallet, category. Search by note and merchant text. |
| FR-038 | S | Duplicate a transaction ("Catat lagi") with today's date. |
| FR-039 | M | **Shortcuts (pintasan):** saved EXPENSE/INCOME templates (own name 1–24 chars shown on the tile, amount, category, wallet, optional note 0–120 chars). One tap on a shortcut (Home "Catat cepat" row or chips in the add sheet) opens the add sheet **pre-filled and still editable**; nothing is saved until "Simpan". Applying one fills the draft note from the shortcut note (empty if none), never from its name. Create via the dedicated "Pintasan baru" form (dashed "Baru" tile or the "Atur" drawer; it only saves a shortcut, never a transaction) or via the "Simpan sebagai pintasan" checkbox (which asks for the name); edit and delete in the "Atur" drawer (undo). |

AC FR-030..033:
- Amount must be > 0 and ≤ 999,999,999,999 (BR-001).
- Saving is possible with amount + category only (other fields have defaults).
- From app open to saved expense: ≤ 4 taps (FAB, digits count as typing, category, save).
- TRANSFER requires source ≠ target (BR-003).

### Dashboard

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-040 | M | Show total balance (sum of included wallets). Tapping hides/shows amounts (privacy toggle). |
| FR-041 | M | Show current period income, expense and net. |
| FR-042 | M | Show top 5 expense categories of the current period as a donut chart + list. |
| FR-043 | M | Show the 5 most recent transactions with "Lihat semua" link. |
| FR-044 | M | Show a banner/badge with the number of pending inbox items. |
| FR-045 | M | Show budget warnings (categories ≥ 80% used). |

### Period

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-050 | M | A "period" is a month starting on a configurable day (1–28, default 1). Example: start day 25 → period 25 Oct – 24 Nov. |
| FR-051 | M | All monthly summaries, budgets and statistics use the configured period. |

### Statistics

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-060 | M | Expense and income breakdown by category for a selected period (donut + ranked list with %). |
| FR-061 | M | Daily expense bar chart for the selected period. |
| FR-062 | M | Comparison with the previous period (absolute and % change per category). |
| FR-063 | S | 6-month trend line of income vs expense. |
| FR-064 | M | Tapping a category opens history filtered to that category and period. |

### Budgets

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-070 | M | Set a monthly limit per EXPENSE category. |
| FR-071 | M | Show spent / limit / remaining with a progress bar per budget. |
| FR-072 | M | Color states: < 80% normal, 80–99% warning, ≥ 100% exceeded (BR-020). |
| FR-073 | M | Local notification when a budget crosses 80% and 100% (once per threshold per period). |
| FR-074 | S | Overall monthly budget (all expenses). |

### Notification detection

Full design in `07-notification-detection.md`.

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-080 | M | Guide the user to grant Notification Access, with a clear explanation of what is read and why. |
| FR-081 | M | User chooses which installed apps are monitored (only supported apps are listed by default). |
| FR-082 | M | Each monitored app can be linked to a default wallet. |
| FR-083 | M | Notifications from monitored apps are parsed on-device into amount, direction, merchant, time. |
| FR-084 | M | Parsed results go to the **Inbox** as PENDING items; nothing is recorded without user confirmation. |
| FR-085 | M | Notifications that are not transactions (OTP, promo, balance info) are discarded and **never stored**. |
| FR-086 | M | Duplicate detection: the same notification reposted is ignored; similar items across apps are flagged. |
| FR-087 | M | Inbox item actions: confirm (one tap), edit then confirm, dismiss. Swipe right = confirm, left = dismiss. |
| FR-088 | M | Merchant memory: after confirming, the merchant → category (and wallet) mapping is remembered and suggested next time. |
| FR-089 | M | Optional heads-up notification from our app: "Rp25.000 di Indomaret — Konfirmasi?" with Confirm action. |
| FR-090 | M | Show detection status in Settings (access granted? last notification received at?). |
| FR-091 | S | Detect a bank debit + e-wallet credit pair of the same amount within 10 minutes and suggest a TRANSFER. |
| FR-092 | M | Raw notification text of inbox items is purged 30 days after the item is resolved. |

### Security

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-100 | M | Optional 6-digit PIN lock. |
| FR-101 | M | Optional biometric unlock (requires PIN as fallback). |
| FR-102 | M | Lock when the app returns from background after a configurable timeout (immediately, 30 s, 1 min, 5 min; default 30 s). |
| FR-103 | M | After 5 wrong PIN attempts, wait 30 s; doubles each further failure (max 15 min). |
| FR-104 | S | "Hide from recents" option (FLAG_SECURE). |

### Backup & export

Full design in `11-backup-export.md`.

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-110 | M | Create an encrypted backup file protected by a user password, saved via the system file picker. |
| FR-111 | M | Restore from a backup file (replaces all data after explicit confirmation). |
| FR-112 | M | Backup reminder notification if no backup in N days (default 7, configurable, can be disabled). |
| FR-113 | M | Export transactions of a chosen date range to CSV. |

### Settings

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-120 | M | Theme: system / light / dark. |
| FR-121 | M | Period start day (FR-050). |
| FR-122 | M | Manage wallets, categories, budgets, monitored apps, security, backup. |
| FR-123 | M | About: version, licenses, privacy statement ("Data tidak pernah meninggalkan perangkat ini"). |
| FR-124 | S | Hide amounts by default on the Dashboard. |

### Credit cards & pay-later

Overview and examples: `18-credit-installments-loans.md`.

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-130 | M | Create a credit wallet (type CREDIT_CARD or PAYLATER) with: name, credit limit, statement day (1–28), due day (1–28), current outstanding. |
| FR-131 | M | Credit wallet summary: outstanding, available limit (bar), current statement amount, due date countdown, status (BR-034). |
| FR-132 | M | "Bayar tagihan": prefilled TRANSFER from a non-credit wallet to the card for the remaining due amount. |
| FR-133 | M | Partial and over-payments are allowed; remaining due updates immediately. |
| FR-134 | S | Statement history: last 6 statements with amount, paid, status. |
| FR-135 | M | Reminder notifications at D−3 and D−1 (09:00) when remaining due > 0. |
| FR-136 | M | Overdue statements are highlighted on Home and in the wallet. |
| FR-137 | M | Home "Tagihan" card: nearest due bill, total instalments due this period, open receivables and debts. |
| FR-138 | M | Statement day ("tutup buku") and due day ("jatuh tempo") are editable at any time from the credit wallet screen (day grid 1–28); bills recalculate immediately. |
| FR-139 | M | Creating a wallet offers types Kartu kredit and Paylater with limit, current debt, statement day and due day. A new credit wallet shows "Belum terbit" until its first statement day (BR-034). |

AC FR-131..133:
- Statement amount follows BR-033 exactly (unbilled instalment principal excluded).
- Paying a bill never creates an EXPENSE (BR-039).

### Instalments (cicilan 0%)

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-140 | M | When the add sheet's wallet is a credit wallet, a "Cicilan" toggle allows tenor 3/6/12 or custom 2–36 months and an optional admin fee. |
| FR-141 | S | Convert an existing credit-wallet expense into an instalment plan. |
| FR-142 | M | Instalment list: title, monthly portion, progress "k/n", remaining principal, next billing month. |
| FR-143 | M | Portions are billed on statements per BR-036 and included in the statement amount. |
| FR-144 | M | Early payoff bills all remaining portions on the next statement (BR-037). |
| FR-145 | M | Deleting the purchase transaction deletes its plan after confirmation. |
| FR-146 | M | "Cicilan bulan ini" total = Σ portions billed on statements due in the current period. |

### Loans (hutang & piutang)

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-150 | M | Record lending money (piutang): person, amount, wallet, date, optional due date, note. |
| FR-151 | M | Record borrowing money (hutang): same fields. |
| FR-152 | M | Record partial or full repayments into/out of any wallet; reject amounts above outstanding. |
| FR-153 | M | Loan list grouped by direction, then person, with outstanding and progress. |
| FR-154 | M | Due-date reminders at D−3 and D−1 for open loans with a due date. |
| FR-155 | M | Loans settle automatically at outstanding 0; LENT loans can be written off ("Ikhlaskan"). |
| FR-156 | M | Loan transactions appear in History but never in income/expense statistics. |
| FR-157 | S | Person name suggestions from previous loans. |
| FR-158 | M | Loan due date: choose when recording (none, 1 week, 2 weeks, 1/2/3 months) and change or clear it later from the loan detail. |

### Investments (simple mode)

Overview: `19-investments.md`.

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-170 | M | Create an investment wallet: name, kind (Reksa dana, Saham, Emas, Deposito, Kripto, Lainnya), initial invested amount. |
| FR-171 | M | Put money in / take money out via TRANSFER to/from the investment wallet (wallet picker shows an "Investasi" section for transfers only). |
| FR-172 | M | "Update nilai": enter the current value with the keypad; stored as a valuation dated today. |
| FR-173 | M | Investment screen (S-20): current value, modal, gain (Rp and %), last update date, actions (Update nilai, Tambah dana, Tarik), timeline of transfers and valuations. |
| FR-174 | M | Dompet tab: "Investasi" group with value and gain % per account; Kekayaan bersih includes investments (BR-053). |
| FR-175 | M | Gains never appear in income/expense statistics or budgets (BR-052). |
| FR-176 | S | Optional monthly reminder to update values (default off). |
| FR-177 | S | Home hero secondary line shows total investment value when non-zero. |

### Net worth

| ID | Pri | Requirement |
|----|-----|-------------|
| FR-160 | M | Home hero shows **Saldo** (non-credit wallets); a line below shows Utang kartu and Piutang/Hutang when non-zero. |
| FR-161 | M | Wallets tab shows **Kekayaan bersih** with a breakdown (BR-043). |
| FR-165 | S | Detect credit card / pay-later transaction notifications and map them to the right credit wallet (07). |

---

## Non-functional requirements

| ID | Category | Requirement |
|----|----------|-------------|
| NFR-001 | Privacy | The app declares **no** `android.permission.INTERNET` permission. Verified by an automated test on the merged manifest. |
| NFR-002 | Privacy | No third-party SDK that collects data (no Firebase, no analytics, no crash reporters). |
| NFR-003 | Security | Database encrypted at rest (SQLCipher). Key protected by Android Keystore. |
| NFR-004 | Performance | Cold start to Dashboard < 1.5 s on a mid-range device (Baseline Profile enabled). |
| NFR-005 | Performance | History list scrolls at 60 fps with 10,000 transactions. |
| NFR-006 | Performance | Notification parsing < 50 ms per notification, off the main thread. |
| NFR-007 | Size | Release APK < 20 MB. |
| NFR-008 | Compatibility | minSdk 26 (Android 8.0), target/compile latest stable SDK. Portrait only in v1. |
| NFR-009 | Reliability | Database migrations are tested; never destructive (`fallbackToDestructiveMigration` is forbidden). |
| NFR-010 | Accessibility | Touch targets ≥ 48 dp; content descriptions on icons; supports font scale up to 200%; WCAG AA contrast. |
| NFR-011 | Localization | All UI strings in `strings.xml` (Indonesian). No hardcoded user-facing text. Currency formatted as `Rp25.000`. |
| NFR-012 | Maintainability | All code passes `./gradlew check` (format, lint, detekt, Konsist, tests, coverage). |
| NFR-013 | Testability | Domain and parser modules are pure Kotlin (no Android dependency). |
| NFR-014 | Battery | No polling. Work only on notification events and WorkManager jobs. |
| NFR-015 | Data integrity | Money stored as `Long` (whole rupiah). Floating point types are forbidden for money. |
