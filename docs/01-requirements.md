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
| FR-035 | M | Delete a transaction with an "Undo" snackbar (5 seconds). |
| FR-036 | M | Transaction history grouped by day, newest first, with daily totals. |
| FR-037 | M | Filter history by: month, type, wallet, category. Search by note and merchant text. |
| FR-038 | S | Duplicate a transaction ("Catat lagi") with today's date. |

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
