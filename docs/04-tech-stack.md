# 04 — Tech Stack

Versions are **not** listed here on purpose: at project setup, use the latest **stable** release of each
library, pin it in `gradle/libs.versions.toml`, and never use dynamic versions (`+`, `latest.release`).
Upgrades happen in dedicated `build:` commits, one library group at a time, with `./gradlew check` green.

## Platform

| Item | Choice | Note |
|------|--------|------|
| Language | Kotlin (latest stable, K2 compiler) | |
| JDK | 21 (toolchain) | `jvmToolchain(21)` in convention plugins |
| Build | Gradle (Kotlin DSL) + version catalog + convention plugins | |
| Android Gradle Plugin | latest stable | |
| minSdk | 26 | `java.time` available natively; adaptive icons |
| target/compileSdk | latest stable | |
| Orientation | portrait | |

## Libraries

| Area | Library | Why |
|------|---------|-----|
| UI | Jetpack Compose (BOM), Material 3 | Modern declarative UI, theming |
| Icons | Material Symbols (bundled vector drawables, rounded style) | Offline, consistent |
| Font | Plus Jakarta Sans (bundled `res/font`, OFL licence) | Beautiful, Indonesian-designed; no downloadable fonts (offline) |
| Navigation | Navigation Compose (type-safe routes) + kotlinx-serialization | |
| Lifecycle | lifecycle-runtime-compose, lifecycle-viewmodel-compose | `collectAsStateWithLifecycle` |
| DI | Hilt (+ hilt-navigation-compose), KSP | |
| Async | Kotlin Coroutines + Flow | |
| Database | Room (KSP) | |
| DB encryption | SQLCipher for Android (`net.zetetic:sqlcipher-android`) + androidx.sqlite | |
| Settings | DataStore Preferences | |
| Background | WorkManager (+ hilt-work) | backup reminder, inbox purge |
| Charts | Vico (Compose, Material 3) | Native Compose charts |
| Biometrics | androidx.biometric | |
| Serialization | kotlinx-serialization-json | backup file, nav routes |
| Collections | kotlinx-collections-immutable | stable types for Compose |
| Startup perf | Baseline Profiles (androidx.baselineprofile) + ProfileInstaller | NFR-004 |
| Splash | androidx.core:core-splashscreen | |

## Testing

| Tool | Purpose |
|------|---------|
| JUnit 5 (Jupiter) | JVM unit tests (pure Kotlin modules + ViewModels) |
| JUnit 4 + AndroidX Test | Instrumented / Robolectric tests (Robolectric needs JUnit 4) |
| kotlinx-coroutines-test | `runTest`, `TestDispatcher` |
| Turbine | Testing `Flow` |
| AssertK | Fluent assertions (Kotlin-first) |
| Robolectric | Compose UI tests and Room tests on JVM |
| Compose UI Test | Interaction tests |
| Roborazzi | Screenshot tests on JVM (Robolectric) |
| Room testing | `MigrationTestHelper` |
| Konsist | Architecture & convention tests |
| Kover | Coverage reports + minimum thresholds |

**Mocking libraries (MockK, Mockito) are forbidden.** Use hand-written fakes in `core:testing` (ADR-007).

## Code quality

| Tool | Purpose |
|------|---------|
| Spotless + ktlint | Formatting (official Kotlin style, Android variant) for `.kt` and `.kts` |
| detekt (+ detekt-formatting disabled; ktlint via Spotless instead) | Static analysis, strict config (`05`) |
| Compose Rules for detekt (`io.nlopez.compose.rules:detekt`) | Compose best practices |
| Android Lint | `warningsAsErrors = true`, `abortOnError = true`, `checkDependencies = true` |
| Qodana Community for Android (JetBrains) | Unused public declarations (dead code across modules) — run in CI |
| Dependency Analysis Gradle Plugin (`com.autonomousapps.dependency-analysis`) | Unused / misdeclared dependencies → fails build |
| Module graph assertion (`com.jraska.module.graph.assertion`) | Enforce module dependency rules |
| Gradle Doctor (optional) | Build health warnings |

## Forbidden dependencies

- Anything requiring `INTERNET` (Firebase, Crashlytics, analytics, ads, OkHttp/Retrofit, Coil network fetching).
- Mocking frameworks (MockK, Mockito).
- RxJava, LiveData (use Flow), Gson/Moshi (use kotlinx-serialization), Joda/ThreeTen (use java.time).
- Accompanist modules that have official replacements.

Adding a new dependency requires: an entry in this file, justification, and owner approval.
