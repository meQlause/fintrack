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
