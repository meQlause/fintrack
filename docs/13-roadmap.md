# 13 — Roadmap

> **For execution use `20-phase.md`** — it breaks every phase below into single-session tasks with files, tests, checks and commit messages.
> This file stays as the high-level summary; tick a phase's boxes here when all its tasks in 20 are done.

Work phase by phase. Inside a phase, take exit criteria top to bottom. A phase is complete when **every**
checkbox is ticked and `./gradlew check` is green. Tick boxes in the same commit that satisfies them.

---

## P0 — Design

- [x] Open questions Q-001 (name), Q-002 (package), Q-005 (accent color) answered.
- [ ] HTML mockups of S-03 Dashboard, S-04 Add sheet, S-05 History, S-07 Inbox, S-08 Statistics, S-09 Wallets (light + dark).
- [ ] Owner approves the visual direction; `08-design-system.md` updated with any changes.

## P1 — Project skeleton & quality gates

Goal: an empty app where **every rule in 05/06 is already enforced** before any feature exists.

- [ ] Gradle project with version catalog, `build-logic` convention plugins (03 → Build logic).
- [ ] All modules from 03 created (empty placeholders contain only what is needed to compile).
- [ ] Spotless + ktlint + `.editorconfig`; `spotlessCheck` in `check`.
- [ ] detekt with strict config (05 §2–5) + Compose rules; config validation on; no baseline.
- [ ] Android Lint: warningsAsErrors, abortOnError, checkDependencies.
- [ ] Kotlin `allWarningsAsErrors`, `explicitApi()` in pure core modules.
- [ ] Dependency Analysis plugin + module graph assertion wired into `check`.
- [ ] Kover with thresholds (06) wired into `check`.
- [ ] Konsist test suite (06 → Architecture tests, full list) — passing on the skeleton.
- [ ] Roborazzi configured; one sample screenshot test of `FintrackTheme` colors.
- [ ] `ManifestNoInternetTest` passing.
- [ ] `.githooks` (pre-commit, commit-msg, pre-push) + `core.hooksPath` documented in README.
- [ ] GitHub Actions CI green.
- [ ] Root `README.md` with build/test commands.
- [ ] A deliberate violation of each rule (long function, short name, `!!`, `@Suppress`, dead code, `android.*` in domain) fails `check` — verified once and noted in the commit body, then removed.

## P2 — Design system & app shell

- [ ] Gradle dependency verification enabled (`verification-metadata.xml`, Q-007).
- [ ] `FintrackTheme` (colors, typography with bundled Plus Jakarta Sans, spacing, shapes, motion).
- [ ] Core components (08 → Core components) with previews + screenshot tests.
- [ ] `core:ui` MoneyText/MoneyFormatter/DateFormatter with unit tests (formatting table in 08).
- [ ] App shell: bottom bar with 4 destinations + FAB, empty placeholder screens, type-safe navigation.

## P3 — Data foundation

- [ ] `core:model` entities/value classes/enums (02).
- [ ] Room database + SQLCipher + Keystore key (10), schema v1 exported.
- [ ] DAOs incl. `observeBalances()` (02 → balance formula) with tests for every transaction type.
- [ ] DataStore settings (02 → Settings).
- [ ] Repositories (domain interfaces + `Local*` implementations) with contract tests against fakes.
- [ ] Default category seeding (FR-004).
- [ ] `PeriodCalculator` (BR-010) with edge-case tests (start day 1, 25, 28; Feb; year boundary).

## P4 — Core manual features

- [ ] Onboarding S-01 (FR-001..004).
- [ ] Wallets S-09 (FR-010..016).
- [ ] Categories S-10 (FR-020..023).
- [ ] Add/Edit transaction sheet S-04 (FR-030..035) — 4-tap path verified by UI test.
- [ ] History S-05 + detail S-06 (FR-036..038).
- [ ] Dashboard S-03 (FR-040..045 except inbox/budget parts).
- [ ] Settings basics: theme, period start day (FR-120, FR-121).

## P5 — Notification detection

- [ ] `core:parser` common utilities (amount, ignore, direction, merchant normalizer) with full test tables (07).
- [ ] Fixtures (anonymized, ADR-013) collected for BCA, GoPay, DANA + parsers implemented.
- [ ] `FintrackNotificationListener` + monitored apps + diagnostics (FR-080..083, FR-090).
- [ ] Process pipeline, fingerprint dedup, merchant memory (FR-084..088).
- [ ] Inbox S-07 incl. swipe actions (FR-087).
- [ ] Heads-up confirm prompt with action button (FR-089).
- [ ] Pairing as transfer (FR-091).
- [ ] Raw-text purge job (FR-092).
- [ ] Remaining supported apps' parsers.

## P6 — Insights

- [ ] Statistics S-08 (FR-060..064).
- [ ] Budgets S-11 + dashboard warnings + alerts (FR-070..074, FR-045).

## P7 — Credit, instalments & loans

- [ ] `PAYLATER` wallet type and credit wallet fields; migration + test (02).
- [ ] `StatementCalculator` (BR-032 … BR-034) with tests for every row of the worked example in 18, month/year boundaries, statementDay ≥ dueDay and ≤ dueDay.
- [ ] `InstallmentScheduler` (BR-035 … BR-037) with tests: rounding, tenor 2 and 36, early payoff, edit/delete.
- [ ] Credit wallet detail S-14 + pay bill S-15 (FR-130 … FR-136).
- [ ] Instalment toggle in S-04 + S-16 (FR-140 … FR-146).
- [ ] Loan entity, LEND/BORROW/COLLECT/REPAY transactions, S-17, Pinjaman tab in S-04 (FR-150 … FR-157).
- [ ] Saldo / Kekayaan bersih (BR-043) on S-03 and S-09; Tagihan card (FR-137).
- [ ] Bill and loan reminders (BR-045, FR-135, FR-154).
- [ ] Statistics and budgets ignore loan transactions and bill payments (BR-039, BR-040) — regression tests.
- [ ] Backup DTOs + validation for installment plans and loans (11).
- [ ] Credit/pay-later notification hints and wallet mapping (FR-165).
- [ ] Investments simple mode: INVESTMENT wallet type, ValuationEntry + migration, `InvestmentCalculator` (BR-051) tests, S-20, Dompet group, net worth (FR-170 … FR-177).
- [ ] Shortcuts: QuickEntry entity, Home "Catat cepat", shortcut drawer, "Simpan sebagai pintasan" (FR-039, BR-047).
- [ ] Wallet detail screen S-19 (FR-017).

## P8 — Security & data safety

- [ ] PIN + lock screen + throttling (FR-100, FR-102, FR-103).
- [ ] Biometric unlock (FR-101), hide from recents (FR-104).
- [ ] Backup/restore with all tests in 11 (FR-110, FR-111).
- [ ] Backup reminder (FR-112), CSV export (FR-113).
- [ ] About screen with privacy statement and licenses (FR-123).

## P9 — Polish & release to friends

- [ ] Baseline profile; cold start < 1.5 s measured (NFR-004).
- [ ] 10,000-transaction scroll performance check (NFR-005).
- [ ] Accessibility pass: TalkBack walkthrough, font scale 200% screenshots (NFR-010).
- [ ] Battery-optimization help page for OEMs (07).
- [ ] Signed release APK, versioning, GitHub Release with Indonesian notes.
- [ ] Installed on ≥ 3 friends' phones; feedback collected as GitHub issues.

## Later (v2 candidates)

Recurring transactions · savings goals · debts/receivables · home-screen widget · smart text input · on-device receipt OCR.
