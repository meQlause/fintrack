# 20 — Phase Checklist (execution plan for AI agents)

This is the **work queue**. An agent (e.g. Claude Haiku) takes the **first unchecked task**, finishes it completely,
ticks it, commits, and stops. `13-roadmap.md` is the high-level summary; this file is what you execute.

The visual and behavioural reference for every screen is the interactive prototype
`design/project/Main.dc.html` (search the file for the screen ID comment, e.g. `S-05 RIWAYAT`). When the prototype and a doc
disagree, **the doc wins**; when a doc is silent, copy the prototype (layout, Indonesian copy, animation feel) and note it in the commit body.

---

## 0. Protocol (read every session)

1. Read `AGENTS.md`, `docs/14-ai-agent-guide.md`, then this file.
2. Pick the **first** task with `- [ ]` in the **earliest** phase. Never skip ahead. Never do two tasks in one session.
3. Read every doc listed in the task's **Read** line before writing anything.
4. Work test-first (06): write the failing test(s) named in **Tests**, run them, see them fail, then implement.
5. Run every command in **Done when**. All must pass. On Windows use `gradlew.bat` (PowerShell) or `./gradlew` (Git Bash).
6. Tick the task (`- [x]`) in this file **in the same commit**.
7. Commit with the exact message in **Commit** (Conventional Commits + doc IDs, ADR-014). Author `meQlause <ardialbrian@gmail.com>`,
   no `Co-Authored-By`, no AI attribution (ADR-012). Commit only the files of this task.
8. Report to the owner in English: task ID, what changed, check output summary, anything uncertain.

**Stop and ask the owner** (do not guess) when:
- a task depends on something the owner must provide (marked **OWNER**),
- a check fails 3 times after honest fixes,
- you would need to edit a protected file (05 §9) or loosen a rule,
- the docs are contradictory or silent on behaviour (not just on layout),
- a library version cannot be determined (use the latest **stable** release from the library's official page; never alpha/beta/rc).

**Never:** add `@Suppress`, baselines, `--no-verify`, the INTERNET permission, mocking libraries, `Double` for money,
`Instant.now()` without `Clock`, hard-coded UI strings/colours/dp in feature modules.

### Task template

```
### T<phase>.<n> — <title>
- [ ] done
- Refs:      doc IDs implemented
- Read:      docs to read first
- Create:    files/folders (paths relative to repo root)
- Steps:     numbered actions
- Tests:     tests to write first (names end with the doc ID)
- Done when: commands that must pass + observable result
- Commit:    exact commit header
```

Conventions used below: `<root>` = `com/meqlause/fintrack` (package `com.meqlause.fintrack`). Module paths follow 03.

---

## P0 — Design ✅

- [x] Open questions Q-001 … Q-017 answered (17).
- [x] Interactive prototype `design/project/Main.dc.html` approved by the owner.
- [x] Logo `design/assets/fintrack-logo.svg`.

---

## P1 — Project skeleton & quality gates

Goal: an empty app where **every rule in 05 and 06 is enforced** before any feature exists.

### T1.1 — Environment check
- [x] done
- Refs: DOC-04
- Read: 04-tech-stack.md
- Steps:
  1. Run `java -version` (must be 21), `echo $ANDROID_HOME` / `$env:ANDROID_HOME` (must point to an Android SDK with the latest stable platform and build-tools), `git config user.name` (must be `meQlause`).
  2. If any is missing, **stop** and tell the owner exactly what to install (JDK 21 Temurin, Android Studio latest stable / SDK).
- Done when: all three print correct values. No files change; tick this box only.
- Commit: `chore: verify development environment [P1, DOC-04]`

### T1.2 — Gradle root, wrapper and version catalog
- [x] done
- Refs: DOC-03, DOC-04
- Read: 03 (Build logic), 04
- Create: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, `gradle/wrapper/*`, `gradlew`, `gradlew.bat`, `gradle/libs.versions.toml`
- Steps:
  1. Generate the wrapper with the latest stable Gradle (`gradle wrapper --gradle-version <latest>`). If Gradle is not installed, **OWNER**: ask the owner to create an "Empty Activity" project in Android Studio named Fintrack with package `com.meqlause.fintrack`, then move its wrapper files here.
  2. `gradle.properties`: `org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8`, `org.gradle.caching=true`, `org.gradle.configuration-cache=true`, `android.useAndroidX=true`, `kotlin.code.style=official`.
  3. `libs.versions.toml`: one entry per library/tool in 04 (versions = latest stable, exact, no `+`), plus plugin aliases.
  4. `settings.gradle.kts`: `pluginManagement { includeBuild("build-logic") }`, repositories `google()`, `mavenCentral()`, `gradlePluginPortal()`; `dependencyResolutionManagement` with `RepositoriesMode.FAIL_ON_PROJECT_REPOS`; `rootProject.name = "fintrack"`.
- Done when: `./gradlew help` succeeds.
- Commit: `build: add gradle wrapper and version catalog [P1, DOC-04]`

### T1.3 — Convention plugins (build-logic)
- [x] done
- Refs: DOC-03 (Build logic)
- Read: 03
- Create: `build-logic/settings.gradle.kts`, `build-logic/convention/build.gradle.kts`, `build-logic/convention/src/main/kotlin/` with plugins:
  `fintrack.android.application`, `fintrack.android.library`, `fintrack.android.feature`, `fintrack.android.room`, `fintrack.android.hilt`, `fintrack.jvm.library`, `fintrack.quality`.
- Steps:
  1. Shared config: `jvmToolchain(21)`, Android `minSdk = 26`, `compileSdk`/`targetSdk` = latest stable, Compose enabled only in application/feature/designsystem/ui.
  2. `fintrack.jvm.library`: `kotlin("jvm")`, `explicitApi()`, JUnit 5 platform.
  3. Kotlin compiler option `allWarningsAsErrors = true` in every plugin.
  4. `fintrack.quality` is applied by all others (filled in T1.6–T1.13; start empty).
- Done when: `./gradlew :build-logic:convention:build` (or `./gradlew help`) succeeds.
- Commit: `build: add convention plugins [P1, DOC-03]`

### T1.4 — Create all modules
- [x] done
- Refs: DOC-03 (Module list), ADR-006
- Read: 03
- Create: every module of 03 (core:model, common, domain, parser, database, datastore, data, notification, security, backup, designsystem, ui, testing; feature:onboarding, dashboard, transaction, inbox, wallet, category, budget, statistics, settings, lock, credit, loan, investment; app) with a `build.gradle.kts` applying the matching convention plugin and the `<root>` package folder.
- Steps: include all in `settings.gradle.kts`. Module dependencies exactly as the table in 03 (nothing more).
- Done when: `./gradlew assembleDebug` succeeds.
- Commit: `build: create module structure [P1, DOC-03]`

### T1.5 — App entry point, manifest and privacy settings
- [x] done
- Refs: NFR-001, NFR-008, DOC-10
- Read: 10 (Offline guarantee, Permissions)
- Create: `app/src/main/AndroidManifest.xml`, `FintrackApplication.kt` (`@HiltAndroidApp`), `MainActivity.kt` (shows text "Fintrack" via `stringResource`), `res/values/strings.xml`, `res/xml/data_extraction_rules.xml`, `res/xml/backup_rules.xml`.
- Steps: `android:allowBackup="false"`, data extraction rules exclude everything, portrait only, **no INTERNET / ACCESS_NETWORK_STATE**.
- Done when: `./gradlew assembleDebug` succeeds; app launches on an emulator showing "Fintrack".
- Commit: `feat(app): add entry point with privacy-safe manifest [P1, NFR-001]`

### T1.6 — Formatting (Spotless + ktlint + .editorconfig)
- [x] done
- Refs: DOC-05 §1
- Read: 05 §1
- Create: `.editorconfig` (exactly as 05 §1), Spotless config in `fintrack.quality`.
- Done when: `./gradlew spotlessCheck` passes; a mis-indented file makes it fail; `./gradlew spotlessApply` fixes it.
- Commit: `build: add spotless and ktlint [P1, DOC-05]`

### T1.7 — detekt (strict) + Compose rules
- [x] done
- Refs: DOC-05 §2–5, ADR-008
- Read: 05 (all)
- Create: `config/detekt/detekt.yml`, detekt wiring in `fintrack.quality`.
- Steps:
  1. Thresholds from 05 §2 (LongMethod 30, LargeClass 200, CyclomaticComplexMethod 10, CognitiveComplexMethod 15, NestedBlockDepth 3, LongParameterList 5/7, TooManyFunctions 15, ReturnCount 2, ComplexCondition 3, MaxLineLength 120).
  2. Naming: `VariableMinLength` 4, `FunctionMinLength` 4; `FunctionNaming` ignores `@Composable`.
  3. Forbidden (05 §5): `ForbiddenSuppress`, `UnsafeCallOnNullableType`, `GlobalCoroutineUsage`, `ForbiddenMethodCall` (println, System.out, printStackTrace, Thread.sleep, runBlocking, System.currentTimeMillis, Instant.now, LocalDate.now, fallbackToDestructiveMigration), `ForbiddenComment` (allow `TODO(#n):`), `WildcardImport`, `MagicNumber`, `SwallowedException`, `TooGenericExceptionCaught`, all `Unused*` rules.
  4. `config.validation = true`, `buildUponDefaultConfig = true`, **no baseline**. Add `io.nlopez.compose.rules:detekt`.
  5. Verify every rule name exists in the installed detekt version (05 §4 note).
- Done when: `./gradlew detekt` passes on the skeleton.
- Commit: `build: add strict detekt config [P1, DOC-05]`

### T1.8 — Android Lint
- [x] done
- Refs: DOC-05, NFR-011
- Create: `config/lint/lint.xml`; lint options in convention plugins: `warningsAsErrors = true`, `abortOnError = true`, `checkDependencies = true`, `UnusedResources` and `HardcodedText` = error.
- Done when: `./gradlew lint` passes.
- Commit: `build: make android lint strict [P1, DOC-05]`

### T1.9 — Dependency hygiene and module graph rules
- [x] done
- Refs: DOC-03 (Dependency rules), DOC-04
- Steps: apply `com.autonomousapps.dependency-analysis` (fail on any issue) and `com.jraska.module.graph.assertion` with: feature ↛ feature, feature ↛ data/database/datastore, no cycles. Wire `buildHealth` and `assertModuleGraph` into `check`.
- Done when: `./gradlew buildHealth assertModuleGraph` pass.
- Commit: `build: enforce dependency and module graph rules [P1, DOC-03]`

### T1.10 — Test infrastructure + core:testing basics
- [x] done
- Refs: DOC-06, ADR-007
- Read: 06
- Create in `core:testing`: `time/FakeClock.kt`, `coroutine/MainDispatcherExtension.kt` (JUnit 5), `coroutine/MainDispatcherRule.kt` (JUnit 4). Configure JUnit 5 for JVM modules, JUnit 4 + Robolectric for Android modules, AssertK, Turbine, coroutines-test.
- Tests: `FakeClockTest` — `given fake clock when advanced then instant moves (DOC-06)`.
- Done when: `./gradlew test` passes.
- Commit: `test: add test infrastructure and fake clock [P1, DOC-06]`

### T1.11 — Konsist architecture tests
- [x] done
- Refs: DOC-06 (Architecture tests), DOC-05 §3–§8, NFR-001
- Create: `app/src/test/kotlin/<root>/architecture/` with every test in the 06 list (ModuleDependency, PureKotlin, NamingSuffix, NamingLength, FileLength 250, OneTopLevelDeclaration, NoSuppress, NoBaseline, MoneyType, ViewModel, ComposableModifier, NoHardcodedDesignValues, DocReference, ManifestNoInternet, ForbiddenDependencies).
- Steps: `ManifestNoInternetTest` reads the merged release manifest (make `test` depend on `processReleaseMainManifest`).
- Done when: `./gradlew :app:test` passes.
- Commit: `test: add konsist architecture tests [P1, DOC-06]`

### T1.12 — Coverage (Kover)
- [x] done
- Refs: DOC-06 (Coverage)
- Steps: Kover in `fintrack.quality`; thresholds per module group from 06; exclusions from 06; `koverVerify` in `check`.
- Done when: `./gradlew koverVerify` passes (empty modules are excluded until they have code).
- Commit: `build: add kover coverage thresholds [P1, DOC-06]`

### T1.13 — Screenshot tests (Roborazzi)
- [x] done
- Refs: DOC-06 (Screenshot tests)
- Create: Roborazzi in `fintrack.android.feature` and for `core:designsystem`; one test `ThemeSwatchScreenshotTest` that renders a placeholder composable in light and dark.
- Done when: `./gradlew recordRoborazziDebug` then `./gradlew verifyRoborazziDebug` pass.
- Commit: `test: add roborazzi screenshot testing [P1, DOC-06]`

### T1.14 — Git hooks
- [x] done
- Refs: DOC-12 (Hooks), ADR-012, ADR-014
- Create: `.githooks/pre-commit` (`spotlessCheck detekt`), `.githooks/commit-msg` (header regex from 12, max 100 chars, rejects `Co-Authored-By`, "Claude", "Generated with", 🤖, author ≠ meQlause), `.githooks/pre-push` (`check`). Make them executable; document `git config core.hooksPath .githooks` in README.
- Done when: a commit with header `feat: test` is rejected; a header with `Co-Authored-By` in the body is rejected; a valid header passes.
- Commit: `build: add git hooks for format, lint and commit rules [P1, ADR-014]`

### T1.15 — CI (GitHub Actions)
- [x] done
- Refs: DOC-12 (CI)
- Create: `.github/workflows/ci.yml` — JDK 21, Gradle cache, `./gradlew check`, `./gradlew assembleRelease`, upload reports on failure. Qodana job (JetBrains Qodana for Android, community) with unused-symbol inspection.
- Done when: the workflow file passes `actionlint` (if available) and the owner confirms a green run after push (**OWNER** pushes).
- Commit: `ci: add github actions workflow [P1, DOC-12]`

### T1.16 — Prove the gates + root README
- [x] done
- Refs: DOC-05, DOC-06, ADR-008
- Steps:
  1. Temporarily add, one at a time: a 40-line function, a variable `val id = 1`, a `!!`, an `@Suppress`, an unused private function, `import android.*` in core:domain, a `Text("hardcoded")` in a feature module. Confirm `./gradlew check` fails for each. Remove all.
  2. Write root `README.md`: what Fintrack is, requirements (JDK 21, SDK), `git config core.hooksPath .githooks`, main commands (`check`, `test`, `spotlessApply`, `recordRoborazziDebug`), link to `docs/`.
- Done when: `./gradlew check` is green; the commit body lists each violation and the rule that caught it.
- Commit: `docs: add readme and verify quality gates [P1, DOC-05]`

---

## P2 — Design system & app shell

### T2.1 — Dependency verification
- [x] done
- Refs: Q-007, DOC-10
- Steps: `./gradlew --write-verification-metadata sha256 help`; commit `gradle/verification-metadata.xml`.
- Done when: `./gradlew check` passes with verification on.
- Commit: `build: enable gradle dependency verification [P2, DOC-10]`

### T2.2 — Sync design tokens doc with the prototype
- [x] done
- Refs: DOC-08
- Read: 08, prototype CSS (`.app.light` / `.app.dark` variables and `this.COLORS` in `design/project/Main.dc.html`)
- Steps: update the colour table in 08 to the **final** prototype palette below (docs-only commit):

| Token | Light | Dark |
|-------|-------|------|
| background | `#F6F6F3` | `#0E1013` |
| surface | `#FFFFFF` | `#16191E` |
| surfaceVariant | `#F1F1EE` | `#1E2228` |
| onSurface (fg) | `#18181B` | `#ECEDEF` |
| onSurfaceVariant (muted) | `#71717A` | `#9BA1AA` |
| faint | `#A1A1AA` | `#6B7280` |
| outline | `#E8E8E4` | `#262A31` |
| primary | `#0B7F74` | `#34D1BF` |
| onPrimary | `#FFFFFF` | `#062A26` |
| primaryContainer | `#E3F4F1` | `#11332F` |
| onPrimaryContainer | `#0B5F57` | `#B9F3EA` |
| income | `#15803D` | `#4ADE80` |
| expense | `#D6325A` | `#FB7A95` |
| transfer | `#2563EB` | `#7AB3FF` |
| warning | `#B45309` | `#FBBF24` |
| error | `#C2410C` | `#FB923C` |

  Category colour catalogue: copy `this.COLORS` from the prototype (light = first value, dark = second). Elevation: cards use a 1 dp hairline border (`outline`), no shadow.
- Done when: 08 matches the table; commit is docs-only.
- Commit: `docs: sync design tokens with final prototype [P2, DOC-08]`

### T2.3 — Theme: colours, typography, spacing, shapes, motion
- [x] done
- Refs: DOC-08, NFR-010
- Create in `core:designsystem`: `theme/FintrackColors.kt`, `theme/FintrackTypography.kt` (Plus Jakarta Sans 400–700 bundled in `res/font`, tabular numbers for amount styles), `theme/FintrackSpacing.kt`, `theme/FintrackShapes.kt`, `theme/FintrackMotion.kt`, `theme/FintrackTheme.kt` (CompositionLocals, light/dark).
- Tests: screenshot test of a token swatch (light, dark, font scale 2.0).
- Done when: `./gradlew check` passes.
- Commit: `feat(designsystem): add fintrack theme tokens [P2, DOC-08]`

### T2.4 — Base components
- [x] done
- Refs: DOC-08 (Core components, principles 6–7)
- Create in `core:designsystem/component/`: `FintrackCard`, `FintrackButton`, `FintrackChip`, `SegmentedControl` (sliding pill), `FormRow` (`Label …… Value ›`, prototype class `frow`), `IconTile`, `FintrackSwitch`, `NumericKeypad` (1–9, 000, 0, backspace), `EmptyState`, `FintrackBottomSheet`, `PickerDrawer` (S-18 shell: fixed header, scrollable body), `UndoSnackbar`.
- Tests: one `@Preview` + screenshot test per component (light/dark); `SegmentedControlTest` click changes selection.
- Done when: `./gradlew check` passes.
- Commit: `feat(designsystem): add base components [P2, DOC-08]`

### T2.5 — Formatters and shared rows (core:ui)
- [x] done
- Refs: DOC-08 (Formatting rules), NFR-011
- Create: `MoneyFormatter` (`Rp1.250.000`, signs with U+2212, compact: `<10rb` → one decimal `4,5rb`, `≥10rb` → `25rb`, `≥1jt` → `1,3jt`), `DateFormatter` (Hari ini, Kemarin, `Sen, 18 Nov`, `HH:mm`), `MoneyText`, `TransactionRow`.
- Tests: table-driven `MoneyFormatterTest` with every example in 08 and the prototype (`4500 → 4,5rb`, `25000 → 25rb`, `1250000 → 1,3jt`), `DateFormatterTest` with a `FakeClock`.
- Done when: `./gradlew :core:ui:check` passes, coverage ≥ thresholds.
- Commit: `feat(ui): add money and date formatters [P2, DOC-08]`

### T2.6 — App shell: navigation + liquid bottom bar
- [ ] done
- Refs: DOC-09 (Navigation map), DOC-03 (Navigation)
- Read: 09 navigation map; prototype `LIQUID BOTTOM BAR`, `.navbar`, `.navblob`, `.fab`
- Create: type-safe routes for 4 tabs (Beranda, Riwayat, Investasi, Dompet) + overlay routes; `FintrackBottomBar` (floating capsule, notch that follows a spring-animated bubble, droplet + ripple, centre FAB with squish/spin/burst); placeholder screens with the shared tab header (32 dp tile + 20 sp title; Beranda uses the logo).
- Tests: UI test: tapping each tab shows its title; screenshot of the bar per selected tab.
- Done when: `./gradlew check` passes; manual run shows the bubble moving with a spring.
- Commit: `feat(app): add navigation shell and liquid bottom bar [P2, DOC-09]`

---

## P3 — Data foundation

### T3.1 — Domain model
- [x] done
- Refs: DOC-02 (all entities and enums)
- Create in `core:model`: value classes, enums, data classes for Wallet, Category, Transaction, Budget, BudgetAlertLog, InboxItem, MerchantRule, MonitoredApp, InstallmentPlan, Loan, ValuationEntry, QuickEntry, Settings.
- Tests: `MoneyTest` (plus/minus/compare, BR-001 bounds helper).
- Done when: `./gradlew :core:model:check` passes.
- Commit: `feat(model): add domain entities [P3, DOC-02]`

### T3.2 — Common: clock, dispatchers, logger
- [x] done
- Refs: DOC-03 (Threading), DOC-10 (Logging)
- Create in `core:common`: `ClockProvider`, `@IoDispatcher`, `@DefaultDispatcher`, `@ApplicationScope`, `AppLogger` (no-op in release; never logs amounts/merchants/notification text).
- Done when: `./gradlew :core:common:check` passes.
- Commit: `feat(common): add clock, dispatchers and logger [P3, DOC-03]`

### T3.3 — Period calculator
- [x] done
- Refs: BR-010, FR-050
- Create: `core:domain/period/PeriodCalculator.kt`.
- Tests: start day 1, 25, 28; February; year boundary; date equal to start day.
- Done when: `./gradlew :core:domain:check` passes.
- Commit: `feat(domain): add period calculator [P3, BR-010]`

### T3.4 — Keystore key + SQLCipher passphrase
- [x] done
- Refs: NFR-003, ADR-009
- Read: 10 (Database encryption)
- Create in `core:security`: `DatabaseKeyProvider` (generates 32 random bytes once, wraps with an AES-GCM Keystore key, stores in `files/db_key.bin`).
- Tests: Robolectric test with a fake key store: same passphrase returned on second call.
- Commit: `feat(security): add database key provider [P3, NFR-003]`

### T3.5 — Room database (schema v1, all tables)
- [x] done
- Refs: DOC-02 (Database), NFR-009, ADR-016, ADR-017
- Create in `core:database`: entities for every table in 02 (incl. installment_plan, loan, valuation_entry, quick_entry), TypeConverters (enums by name), DAOs, `FintrackDatabase` with SQLCipher factory, exported schema in `core/database/schemas/`.
- Tests: in-memory DAO tests for insert/query of each table; FK RESTRICT behaviour (BR-008).
- Done when: `./gradlew :core:database:check` passes; schema JSON committed.
- Commit: `feat(database): add room schema v1 [P3, DOC-02]`

### T3.6 — Balance query
- [x] done
- Refs: DOC-02 (Wallet balance formula), BR-004, BR-040
- Create: `WalletDao.observeBalances()` — one SQL query covering EXPENSE, INCOME, TRANSFER (+fee), LEND, BORROW, COLLECT, REPAY.
- Tests: one test per transaction type + a mixed scenario; transfer fee deducted from source only.
- Commit: `feat(database): add wallet balance query [P3, BR-004]`

### T3.7 — Settings (DataStore)
- [x] done
- Refs: DOC-02 (Settings)
- Create in `core:datastore`: `SettingsDataSource` with every key/default from 02 (+ `investReminder` false, `histView`).
- Tests: defaults; write/read round trip.
- Commit: `feat(datastore): add settings data source [P3, DOC-02]`

### T3.8 — Repositories, fakes and contract tests
- [x] done
- Refs: DOC-03 (core:domain, core:data), ADR-007, DOC-06 (Fakes)
- Create: repository interfaces in `core:domain/repository/` (Wallet, Category, Transaction, Budget, Inbox, MerchantRule, MonitoredApp, InstallmentPlan, Loan, Valuation, QuickEntry, Settings); `Local*Repository` in `core:data`; `Fake*Repository` in `core:testing`; abstract `*RepositoryContractTest` run against both.
- Done when: `./gradlew check` passes; coverage thresholds met.
- Commit: `feat(data): add repositories with fakes and contract tests [P3, ADR-007]`

### T3.9 — Default categories seeding
- [x] done
- Refs: FR-004, DOC-02 (Default categories, incl. "Biaya & Admin")
- Create: `SeedDefaultCategoriesUseCase`; names from `strings.xml`.
- Tests: seeds once; second run does nothing; kinds correct.
- Commit: `feat(domain): seed default categories [P3, FR-004]`

---

## P4 — Core manual features

Each screen task: UiState (all states from 09) → ViewModel + tests → stateless Screen with previews (light/dark) → Route → navigation → screenshot tests → UI test of the main interaction. Copy Indonesian strings from the prototype into `strings.xml`.

### T4.1 — Onboarding (S-01)
- [ ] done · Refs: FR-001 … FR-004 · Commit: `feat(onboarding): add onboarding flow [P4, FR-001]`

### T4.2 — Wallet picker drawer (S-18)
- [ ] done
- Refs: DOC-09 S-18, DOC-08 principle 6
- Create in `core:ui`: `WalletPickerDrawer` (sections Tunai · Debit · Kartu kredit · Paylater · Investasi, filters per use, "Sudah dipilih", radio), `DayGridDrawer` (1–28), `OptionsDrawer`.
- Commit: `feat(ui): add shared picker drawers [P4, DOC-09]`

### T4.3 — Wallets: Dompet tab + create/edit sheet (S-09)
- [ ] done · Refs: FR-010 … FR-016, FR-139, FR-170 · Note: type and investment kind are chosen via drawers (no chip piles). Net worth block (BR-053) arrives in T7.8.
- Commit: `feat(wallet): add wallet list and editor [P4, FR-010]`

### T4.4 — Wallet detail (S-19)
- [ ] done · Refs: FR-017 · Actions Pakai / Transfer; "Lihat semua (N)" expands in place.
- Commit: `feat(wallet): add wallet detail screen [P4, FR-017]`

### T4.5 — Categories (S-10)
- [ ] done · Refs: FR-020 … FR-023, BR-008 · Commit: `feat(category): add category management [P4, FR-020]`

### T4.6 — Add / edit transaction sheet (S-04)
- [ ] done
- Refs: FR-030 … FR-035, BR-001 … BR-003, BR-006, BR-009
- Read: 09 S-04 (incl. "Bayar pakai" row and left-to-right transfer), prototype `isAddSheet`
- Tests: validation messages; 4-tap path UI test (FR-033 AC); transfer source ≠ target; credit wallet not allowed as transfer source (BR-039).
- Commit: `feat(transaction): add transaction editor sheet [P4, FR-030]`

### T4.7 — Shortcuts (pintasan)
- [ ] done · Refs: FR-039, BR-047 · Home tiles, header "⚡ Pintasan" drawer (3-column grid), "Simpan sebagai pintasan", manager drawer with undo.
- Commit: `feat(transaction): add quick-entry shortcuts [P4, FR-039]`

### T4.8 — History list (S-05)
- [ ] done
- Refs: FR-036, FR-037, DOC-09 S-05
- Include: month strip with sliding indicator, month/year/day picker sheet, type tabs with underline, wallet/category/day filter chips, search, focus-pull transition (no sideways motion), swipe to change month, illustrated empty state.
- Commit: `feat(transaction): add history screen [P4, FR-036]`

### T4.9 — Transaction detail (S-06)
- [ ] done · Refs: FR-034, FR-035, FR-038 · Commit: `feat(transaction): add transaction detail [P4, FR-034]`

### T4.10 — Home (S-03)
- [ ] done
- Refs: FR-040 … FR-045 (inbox/budget parts later), DOC-09 Home simplification, DOC-08 principle 7
- Include: hero (Saldo + count-up, hide toggle, wallet cards, share bar), summary pill → Ringkasan drawer (sections sorted, fixed header), Catat cepat, category card → Kategori drawer, recent list.
- Commit: `feat(dashboard): add home screen [P4, FR-040]`

### T4.11 — Settings basics (S-12)
- [ ] done · Refs: FR-120, FR-121 · Commit: `feat(settings): add theme and period settings [P4, FR-120]`

---

## P5 — Notification detection

### T5.1 — Parser utilities
- [ ] done
- Refs: DOC-07 (Parsing rules), FR-085
- Create in `core:parser`: models, `IndonesianAmountParser`, `IgnoreClassifier`, `DirectionClassifier`, `MerchantNormalizer`, fixture loader + parameterised fixture test.
- Tests: every row of the amount table and keyword tables in 07.
- Commit: `feat(parser): add parsing utilities [P5, FR-083]`

### T5.2 — Collect fixtures (**OWNER**)
- [ ] done
- Refs: DOC-07, ADR-013
- Steps: ask the owner for ≥ 3 real notifications per type (debit, credit, ignored) for BCA, GoPay, DANA (+ card/pay-later if available). Anonymise (names, account numbers, references, phone → `XXXX`; card last 4 → `0000`). Save as JSON fixtures (06 format). **Do not invent fixtures.**
- Commit: `test(parser): add anonymised fixtures for first apps [P5, DOC-07]`

### T5.3 — Parsers: BCA, GoPay, DANA
- [ ] done · Refs: FR-083, FR-165 · One parser per commit is fine (split into T5.3a/b/c if needed).
- Commit: `feat(parser): add bca, gopay and dana parsers [P5, FR-083]`

### T5.4 — Listener service, monitored apps, diagnostics
- [ ] done · Refs: FR-080 … FR-082, FR-090, DOC-07 (Android service details) · Commit: `feat(notification): add notification listener [P5, FR-080]`

### T5.5 — Detection pipeline
- [ ] done · Refs: FR-084, FR-086, FR-088, FR-091, BR-011 … BR-013 · fingerprint dedup, merchant memory, pairing.
- Commit: `feat(domain): add detected transaction pipeline [P5, FR-084]`

### T5.6 — Inbox (S-07)
- [ ] done · Refs: FR-087, FR-089 · swipe right/left, category/wallet change, pair card, heads-up prompt with action.
- Commit: `feat(inbox): add detected transactions inbox [P5, FR-087]`

### T5.7 — Raw text purge job
- [ ] done · Refs: FR-092 · Commit: `feat(notification): purge resolved raw text [P5, FR-092]`

---

## P6 — Insights

### T6.1 — Statistics view inside Riwayat
- [ ] done · Refs: FR-060 … FR-064, DOC-09 (S-08 change note) · "Daftar | Statistik" segmented; follows the month strip; categories collapsed to top 3.
- Commit: `feat(statistics): add statistics view in history [P6, FR-060]`

### T6.2 — Budgets (S-11) + alerts
- [ ] done · Refs: FR-070 … FR-074, BR-020, BR-021 · Commit: `feat(budget): add budgets and alerts [P6, FR-070]`

---

## P7 — Credit, instalments, loans, investments

### T7.1 — StatementCalculator
- [ ] done
- Refs: BR-031 … BR-034, BR-036 (purchase-date rule), BR-044
- Tests: every line of the worked example in 18; NOT_ISSUED for a new wallet (since date); OVERDUE; partial payment; statementDay ≥ / < dueDay; year boundary.
- Commit: `feat(domain): add credit statement calculator [P7, BR-033]`

### T7.2 — InstallmentScheduler
- [ ] done · Refs: BR-035 … BR-038 · Tests: rounding (1.000.000 / 3), tenor 2 and 36, early payoff, purchase after cut-off.
- Commit: `feat(domain): add instalment scheduler [P7, BR-036]`

### T7.3 — Credit wallet screens (S-14, S-15)
- [ ] done · Refs: FR-130 … FR-139 · bill card, pay sheet, limit bar, "Siklus" rows with day-grid drawers.
- Commit: `feat(credit): add credit wallet and bill payment [P7, FR-131]`

### T7.4 — Instalments in the editor + S-16
- [ ] done · Refs: FR-140 … FR-146 · tenor via drawer, admin fee, schedule sheet, "Lunasi sekarang".
- Commit: `feat(credit): add instalment plans [P7, FR-140]`

### T7.5 — Loans (S-17) + Pinjaman tab in the editor
- [ ] done · Refs: FR-150 … FR-158, BR-040 … BR-042, BR-046 · grouped loan form, due-date drawer, repayments, write-off.
- Commit: `feat(loan): add loans and repayments [P7, FR-150]`

### T7.6 — Investments (S-20) + Investasi tab
- [ ] done
- Refs: FR-170 … FR-177, BR-050 … BR-055, ADR-017
- Create: `InvestmentCalculator` (timestamps, not just dates — BR-051), valuation sheet with live gain preview, portfolio tab (total, allocation by kind, accounts, activity).
- Tests: worked example in 19; same-day withdraw after an update keeps the gain.
- Commit: `feat(investment): add investment tracking [P7, FR-170]`

### T7.7 — Tagihan card on Home
- [ ] done · Refs: FR-137, FR-146 · Commit: `feat(dashboard): add bills card [P7, FR-137]`

### T7.8 — Net worth (Dompet) + Ringkasan drawer data
- [ ] done · Refs: FR-160, FR-161, BR-043, BR-053 · Aset vs Utang bar and columns.
- Commit: `feat(wallet): add net worth breakdown [P7, BR-053]`

### T7.9 — Reminders (bills, loans, investments)
- [ ] done · Refs: BR-045, BR-055, FR-135, FR-154, FR-176 · Commit: `feat(notification): add due-date and investment reminders [P7, BR-045]`

### T7.10 — Statistics exclusions (regression)
- [ ] done · Refs: BR-039, BR-040, BR-052 · tests that bill payments, loan movements and investment transfers/gains never appear in statistics or budgets.
- Commit: `test(domain): guard statistics against transfers and loans [P7, BR-040]`

### T7.11 — Credit / pay-later detection hints
- [ ] done · Refs: FR-165, DOC-07 (Credit cards & pay-later) · Commit: `feat(parser): map card and paylater notifications [P7, FR-165]`

---

## P8 — Security & data safety

### T8.1 — PIN + lock screen + throttling (S-02)
- [ ] done · Refs: FR-100, FR-102, FR-103 · Commit: `feat(lock): add pin lock [P8, FR-100]`

### T8.2 — Biometrics + hide from recents
- [ ] done · Refs: FR-101, FR-104 · Commit: `feat(lock): add biometric unlock [P8, FR-101]`

### T8.3 — Encrypted backup & restore
- [ ] done · Refs: FR-110, FR-111, DOC-11 (all tests listed there) · includes plans, loans, valuations, quick entries.
- Commit: `feat(backup): add encrypted backup and restore [P8, FR-110]`

### T8.4 — Backup reminder + CSV export
- [ ] done · Refs: FR-112, FR-113 · Commit: `feat(backup): add reminder and csv export [P8, FR-113]`

### T8.5 — About & privacy screen
- [ ] done · Refs: FR-123 · Commit: `feat(settings): add about and privacy screen [P8, FR-123]`

---

## P9 — Polish & release to friends

### T9.1 — Performance: baseline profile, cold start, 10k list
- [ ] done · Refs: NFR-004, NFR-005 · Commit: `perf(app): add baseline profile [P9, NFR-004]`

### T9.2 — Accessibility pass
- [ ] done · Refs: NFR-010 · TalkBack walkthrough notes in the commit body; font-scale 2.0 screenshots.
- Commit: `fix(app): accessibility pass [P9, NFR-010]`

### T9.3 — Battery-optimisation help page
- [ ] done · Refs: DOC-07 (OEM battery killers) · Commit: `feat(settings): add battery optimisation help [P9, DOC-07]`

### T9.4 — Launcher icon from the logo
- [ ] done · Refs: DOC-08 (Logo) · adaptive icon from `design/assets/fintrack-logo.svg`.
- Commit: `feat(app): add launcher icon [P9, DOC-08]`

### T9.5 — Signed release (**OWNER**)
- [ ] done · Refs: DOC-12 (Releases), ADR-013 · owner creates the keystore; agent wires `keystore.properties` (git-ignored) and the release workflow; tag `v1.0.0`.
- Commit: `build: add release signing [P9, DOC-12]`

### T9.6 — Friends beta
- [ ] done · Install on ≥ 3 friends' phones; collect feedback as GitHub issues (**OWNER**).
