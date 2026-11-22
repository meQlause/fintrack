# 16 — Architecture Decision Records

Format: context → decision → consequences. Status: Accepted / Superseded by ADR-xxx.
Changing a decision = add a new ADR that supersedes the old one (owner approval required). Never delete.

---

### ADR-001 — Android only, Kotlin + Jetpack Compose
- **Status:** Accepted (2026-11-22)
- **Context:** Notification reading is impossible on iOS. Owner and friends use Android.
- **Decision:** Native Android with Kotlin and Jetpack Compose (Material 3).
- **Consequences:** Best access to NotificationListenerService and platform APIs; no iOS version.

### ADR-002 — Fully offline, no INTERNET permission
- **Status:** Accepted (2026-11-22)
- **Context:** Financial data is sensitive; trust is a key selling point; no server cost.
- **Decision:** No network permission, no account, no cloud sync, no third-party SDKs that phone home.
- **Consequences:** Backup is user-driven (encrypted file). No crash reports — bugs come from friends' feedback.

### ADR-003 — Notification detection with mandatory confirmation (Inbox)
- **Status:** Accepted (2026-11-22)
- **Context:** Parsing can be wrong; duplicates and transfers between own wallets are common.
- **Decision:** Detected transactions go to an Inbox and need user confirmation (one tap/swipe).
- **Consequences:** Data stays correct; small friction, minimized by suggestions and heads-up actions.

### ADR-004 — Rule-based on-device parsing (regex templates per app)
- **Status:** Accepted (2026-11-22)
- **Context:** Must be offline, fast, deterministic and testable.
- **Decision:** Per-app parsers with regex templates + shared classifiers, verified by fixtures.
- **Consequences:** New formats require code updates; ML/LLM may be considered later (on-device only).

### ADR-005 — Money as Long whole rupiah, IDR only
- **Status:** Accepted (2026-11-22)
- **Context:** Floating point is unsafe for money; IDR has no practical sub-unit; multi-currency is a non-goal.
- **Decision:** `Money(rupiah: Long)`; amounts with decimals are truncated when parsed.
- **Consequences:** Multi-currency later requires a migration (currency column + minor units).

### ADR-006 — Multi-module architecture (Now in Android style)
- **Status:** Accepted (2026-11-22)
- **Context:** AI-written code needs strong boundaries; pure-Kotlin modules test fast.
- **Decision:** Modules per 03, dependency rules enforced by Konsist and module-graph assertion.
- **Consequences:** More Gradle setup up front (convention plugins); clearer ownership and faster tests.

### ADR-007 — Fakes instead of mocking libraries
- **Status:** Accepted (2026-11-22)
- **Context:** Mocks couple tests to implementation details and are easy for AI to misuse to make tests pass.
- **Decision:** Hand-written fakes in `core:testing`, verified by contract tests. MockK/Mockito forbidden.
- **Consequences:** Some extra code; much more meaningful tests.

### ADR-008 — Strict, machine-enforced code limits
- **Status:** Accepted (2026-11-22)
- **Context:** Code is mostly AI-written; rules must be checked automatically.
- **Decision:** Limits in 05 (30-line functions, 250-line files, 120-char lines, ≥ 4-char names, zero dead code,
  no suppressions, no baselines), enforced via ktlint, detekt, Android Lint, Konsist, Kover, Qodana.
- **Consequences:** More, smaller files. Loosening requires a new ADR.

### ADR-009 — Room + SQLCipher with Keystore-wrapped key
- **Status:** Accepted (2026-11-22)
- **Context:** Data at rest must be protected; background listener must write while app UI is locked.
- **Decision:** SQLCipher passphrase wrapped by a non-auth-bound Keystore key; PIN protects the UI.
- **Consequences:** Slight performance and APK size cost; good protection against file extraction.

### ADR-010 — State-based one-off events in ViewModels
- **Status:** Accepted (2026-11-22)
- **Context:** Channels/SharedFlow for events can drop events across configuration changes.
- **Decision:** Model events in UiState and clear via callbacks (Android architecture guidance).
- **Consequences:** Slightly more state fields; no lost events.

### ADR-011 — Documentation and code in English, UI in Indonesian
- **Status:** Accepted (2026-11-22)
- **Context:** English identifiers/docs are industry standard and best understood by AI models; users are Indonesian.
- **Decision:** English for docs, code, commits; Indonesian for all user-facing strings; glossary maps terms.
- **Consequences:** Glossary (15) must be kept up to date.

### ADR-012 — Commits authored solely by the owner, no AI co-author
- **Status:** Accepted (2026-11-22)
- **Context:** Owner requirement.
- **Decision:** Author/committer `meQlause <ardialbrian@gmail.com>`; `Co-Authored-By` and AI attribution forbidden; enforced by `commit-msg` hook.
- **Consequences:** —

### ADR-013 — Public GitHub repository
- **Status:** Accepted (2026-11-22)
- **Context:** Owner chose a public repo; APKs are shared with friends via GitHub Releases.
- **Decision:** The repository is public. Therefore:
  1. **No real personal data ever enters the repo.** Parser fixtures must be fully anonymized (names, account
     numbers, reference numbers, phone numbers → `XXXX`; amounts may stay). Reviewing fixtures for leaks is part of
     every parser commit.
  2. No secrets: signing keystore and passwords only in git-ignored `keystore.properties` and GitHub Actions secrets.
  3. Screenshots in tests/docs use fake test data only.
  4. Secret scanning and Dependabot alerts enabled on GitHub.
- **Consequences:** Anyone can read the code — which also supports the privacy promise (verifiable offline app).

### ADR-014 — Doc IDs in commits, comments and tests
- **Status:** Accepted (2026-11-22)
- **Context:** The owner wants to go straight from a commit or a comment to the spec that explains it.
- **Decision:** Commit headers end with `[IDs]` (12), behavioural comments/KDoc start with an ID (05 §8),
  test names end with `(ID)` (06). Enforced by the `commit-msg` hook and Konsist `DocReferenceTest`.
- **Consequences:** Every change must be traceable to the docs; unspecified behaviour surfaces early as a question.

### ADR-015 — Spending recognised at purchase; bill payments are transfers
- **Status:** Accepted (2026-11-22)
- **Context:** Credit cards, pay-later and instalments separate the moment of buying from the moment of paying.
  Counting both double-counts spending.
- **Decision:** An EXPENSE is recorded once, at purchase, on the credit wallet (full principal for instalments).
  Paying a bill is a TRANSFER. Statement amounts exclude unbilled instalment portions (BR-033). No interest or late fees;
  an optional one-off admin fee is a normal expense (BR-038).
- **Consequences:** Statistics show what was bought, when. Cash-flow views ("Cicilan bulan ini", bill amounts) are computed separately.

### ADR-016 — Loans as transaction types linked to a Loan entity
- **Status:** Accepted (2026-11-22)
- **Context:** Lending/borrowing moves real money between wallets but is neither income nor expense.
- **Decision:** New transaction types LEND, BORROW, COLLECT, REPAY with a required `loanId`; a Loan row holds person,
  direction, due date and status. Balances use the single transaction table; statistics exclude these types (BR-040).
- **Consequences:** One balance formula for everything; history shows loan movements; no separate payments table.

### ADR-017 — Simple investment tracking with manual valuations
- **Status:** Accepted (2026-11-22). Supersedes the "no investments" non-goal in 00-vision.
- **Context:** Owner wants investments visible in net worth. The app is offline, so no live prices.
- **Decision:** INVESTMENT wallet type; money in/out via transfers; current value entered manually as ValuationEntry;
  gains shown only on investment screens (not income). Optional monthly "update nilai" reminder, default off.
- **Consequences:** Accurate enough for net worth without per-holding complexity; a detailed per-holding mode can be added later.

### ADR-018 — Portrait-only app; lint orientation check disabled
- **Status:** Accepted (2026-11-22), approved by the owner.
- **Context:** The app is portrait-only (10, T1.5). Lint's `LockedOrientationActivity` (error) asks for `unspecified` or
  `fullSensor` so that Chrome OS and large screens can rotate; that conflicts with the product rule.
- **Decision:** Keep `android:screenOrientation="portrait"`. Disable only the `LockedOrientationActivity` issue and the
  `DiscouragedApi` warning on `app/src/main/AndroidManifest.xml` (the same portrait setting) in `config/lint/lint.xml`.
  All other lint errors stay fatal.
- **Consequences:** The app does not rotate on tablets or Chrome OS. Revisit only if the product rule changes.

### ADR-019 — Kotlin pinned to 2.3.21 until KSP supports 2.4
- **Status:** Accepted (2026-11-22), approved by the owner.
- **Context:** Room and Hilt need KSP. KSP has no release for Kotlin 2.4 yet (latest is 2.3.14). Kapt does not work with
  AGP 9's built-in Kotlin; the legacy path needs AGP's old DSL, which the convention plugins do not use.
- **Decision:** Pin Kotlin 2.3.21 and KSP 2.3.14 (04 "latest stable" applies to the latest that works with KSP). Disable
  only the version-lag lint checks for Kotlin artifacts at 2.3.21 in `config/lint/lint.xml`: `NewerVersionAvailable`
  (Kotlin and the Kotlin Android plugin) and `GradleDependency` for `org.jetbrains.kotlin:*`.
  Upgrade to Kotlin 2.4.x in a dedicated `build:` commit once KSP ships for 2.4.
- **Consequences:** One known version lag. The upgrade is tracked as a follow-up, not hidden.

### ADR-020 — Empty Android modules exempt from the "should be a JVM project" advice
- **Status:** Accepted (2026-11-22), owner's standing instruction to follow the recommendation.
- **Context:** Doc 03 makes several modules Android libraries (database, security, notification, designsystem, and others)
  that have no Android code yet. The dependency-analysis plugin flags them as "should be a JVM project".
- **Decision:** Exempt only that advice, only for the Android modules listed in the root `build.gradle.kts`. Every other
  dependency-analysis issue stays fatal. A module leaves the list in the task that adds its first Android file.
  The list must be empty by the end of P4.
- **Consequences:** Modules keep their 03 types. Modules declare only the dependencies their code uses, so the 03 table
  defines the allowed edges, and `assertModuleGraph` enforces them.

### ADR-021 — AGP pinned to 9.4.1 so Qodana can run
- **Status:** Accepted (2026-11-22), approved by the owner.
- **Context:** The owner requires Qodana in CI (12). Qodana's Android linter 2026.3 (the newest image) supports the
  AGP 9.4 line (it reports 9.4.0 as the latest it knows) and refuses to open a project on AGP 9.5.x. 04 asks for the
  latest stable AGP, which is 9.5.2.
- **Decision:** Pin AGP to 9.4.1, the newest 9.4.x release. Disable only the `AndroidGradlePluginVersion` lint check
  for `com.android.application`, `com.android.library` and `com.android.tools.build:gradle` at 9.4.1 in
  `config/lint/lint.xml`. Qodana uses the recommended profile with the unused-symbol inspection and
  `failThreshold: 0`, so any finding fails CI.
- **Consequences:** One known version lag. Upgrade AGP in a dedicated `build:` commit once a Qodana image supports
  AGP 9.5.
