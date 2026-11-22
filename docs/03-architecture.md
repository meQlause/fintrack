# 03 — Architecture

## Overview

- **Pattern:** MVVM with a clean-architecture-inspired layering, unidirectional data flow (UDF).
- **Structure:** Gradle multi-module, modelled after Google's *Now in Android* sample.
- **Data:** offline only. Room is the single source of truth; UI observes `Flow`s.
- **DI:** Hilt.

```
┌──────────────────────────── app ────────────────────────────┐
│  MainActivity, NavHost, Hilt Application, top-level theme    │
└───────────────┬──────────────────────────────────────────────┘
                │ depends on
┌───────────────▼──────────── feature:* ──────────────────────┐
│  Screens (Compose) + ViewModels. One module per feature.     │
└───────────────┬──────────────────────────────────────────────┘
                │ depends on
┌───────────────▼──────────── core:domain ────────────────────┐
│  Use cases + repository INTERFACES. Pure Kotlin.             │
└───────────────┬──────────────────────────────────────────────┘
                │ depends on
┌───────────────▼──────────── core:model ─────────────────────┐
│  Data classes, value classes, enums. Pure Kotlin.            │
└──────────────────────────────────────────────────────────────┘

core:data implements core:domain interfaces using core:database / core:datastore.
app wires core:data into the graph via Hilt. Features never see core:data.
```

## Module list

| Module | Type | Purpose | May depend on |
|--------|------|---------|---------------|
| `app` | Android application | Entry point, navigation graph, Hilt root, wiring | all feature, all core |
| `build-logic` | Gradle included build | Convention plugins (shared Gradle config) | — |
| `core:model` | Kotlin JVM | Entities, value classes, enums | — |
| `core:common` | Kotlin JVM | Dispatchers qualifiers, `Clock` provider interface, small utils | — |
| `core:domain` | Kotlin JVM | Use cases, repository interfaces, validation, period math | model, common |
| `core:parser` | Kotlin JVM | Notification text → `ParsedNotification` (07) | model, common |
| `core:database` | Android library | Room DB, entities, DAOs, migrations, SQLCipher | model |
| `core:datastore` | Android library | DataStore settings | model |
| `core:data` | Android library | Repository implementations, mappers | domain, database, datastore, model, common |
| `core:notification` | Android library | `NotificationListenerService`, posting our own notifications | domain, parser, model, common |
| `core:security` | Android library | PIN hashing, Keystore, biometric helper | common |
| `core:backup` | Android library | Backup/restore/CSV (11) | domain, model, security, common |
| `core:designsystem` | Android library | Theme, colors, typography, base components, icons | — |
| `core:ui` | Android library | Shared feature UI (MoneyText, TransactionRow, formatters) | designsystem, model |
| `core:testing` | Android library | Fakes, test data builders, rules, Hilt test runner | domain, model, common |
| `feature:onboarding` | Android library | FR-001..004 | domain, model, ui, designsystem |
| `feature:dashboard` | Android library | FR-040..045 | same |
| `feature:transaction` | Android library | add/edit sheet, history (FR-030..038) | same |
| `feature:inbox` | Android library | FR-084..091 UI | same |
| `feature:wallet` | Android library | FR-010..016 | same |
| `feature:category` | Android library | FR-020..023 | same |
| `feature:budget` | Android library | FR-070..074 | same |
| `feature:statistics` | Android library | FR-060..064 | same |
| `feature:settings` | Android library | FR-100..124 UI | same |
| `feature:lock` | Android library | PIN/biometric lock screen | same + security |
| `feature:credit` | Android library | Credit wallets, statements, bill payment, instalments (FR-130..146) | domain, model, ui, designsystem |
| `feature:loan` | Android library | Hutang & piutang (FR-150..157) | domain, model, ui, designsystem |
| `feature:investment` | Android library | Investment accounts, valuations (FR-170..177) | domain, model, ui, designsystem |

Statement and instalment math (BR-032 … BR-037) lives in `core:domain/credit/` as pure functions (`StatementCalculator`,
`InstallmentScheduler`) so it can be tested exhaustively without Android. Reminders (BR-045) are a daily WorkManager job in `core:notification`.

Test-only dependency: every module may use `core:testing` in `testImplementation` / `androidTestImplementation`.

## Dependency rules (enforced)

1. `feature:*` must **not** depend on another `feature:*`. Cross-feature navigation goes through
   navigation lambdas wired in `app`.
2. `feature:*` must **not** depend on `core:data`, `core:database`, `core:datastore`.
3. `core:model`, `core:common`, `core:domain`, `core:parser` are **pure Kotlin** (`kotlin("jvm")`),
   no `android.*` imports.
4. No dependency cycles.
5. `core:designsystem` depends on nothing in the project.

Enforcement: Konsist architecture tests (`06-testing.md`) + the module graph assertion plugin
(`com.jraska.module.graph.assertion`) configured in `build-logic`.

## Package naming

Root package and applicationId: `com.meqlause.fintrack` (final, Q-002 — must never change after release).

```
com.meqlause.fintrack.<layer>.<module>[.<subpackage>]
e.g. com.meqlause.fintrack.feature.transaction.edit
     com.meqlause.fintrack.core.domain.usecase
```

## Inside a feature module

```
feature/transaction/src/main/kotlin/com/meqlause/fintrack/feature/transaction/
├── navigation/
│   └── TransactionNavigation.kt          // route definitions + NavGraphBuilder extension
├── history/
│   ├── HistoryRoute.kt                   // stateful: gets ViewModel, collects state
│   ├── HistoryScreen.kt                  // stateless: (uiState, callbacks) -> UI, has @Preview
│   ├── HistoryViewModel.kt
│   ├── HistoryUiState.kt
│   └── component/                        // small composables used only by this screen
│       ├── DayHeader.kt
│       └── HistoryFilterBar.kt
└── edit/
    ├── EditTransactionRoute.kt
    ├── EditTransactionScreen.kt
    ├── EditTransactionViewModel.kt
    ├── EditTransactionUiState.kt
    └── component/
        └── AmountKeypad.kt
```

Rules:
- **One public top-level declaration per file**, file named after it (private helpers allowed).
- `Route` is the only composable that touches the ViewModel. `Screen` receives plain data + lambdas → previewable and testable.
- Everything not needed outside the module is `internal`.

## Inside core:domain

```
core/domain/src/main/kotlin/com/meqlause/fintrack/core/domain/
├── repository/        // interfaces: TransactionRepository, WalletRepository, ...
├── usecase/           // one class per use case: AddTransactionUseCase, ...
├── validation/        // TransactionValidator, ValidationError sealed interface
└── period/            // PeriodCalculator (BR-010)
```

Use case shape:

```kotlin
class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val validator: TransactionValidator,
) {
    suspend operator fun invoke(draft: TransactionDraft): AddTransactionResult { ... }
}
```

- `core:domain` uses `javax.inject:javax.inject` annotations only (pure Kotlin, no Hilt/Android).
- Use cases return a **sealed result type** for expected failures (validation). Exceptions are only for programmer errors / IO failures.

## Data flow (UDF)

```
User action → Screen callback → ViewModel function → UseCase → Repository → Room
                                                                            │
UI ← Screen(uiState) ← ViewModel StateFlow<UiState> ← UseCase Flow ← DAO Flow
```

ViewModel rules:
- Exposes exactly one `val uiState: StateFlow<XxxUiState>` built with `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial)`.
- One-off events (navigate, snackbar) are modelled **in state** (e.g. `val userMessage: UserMessage?`) and cleared via a `onMessageShown()` callback. No `SharedFlow`/`Channel` for events.
- Never exposes `MutableStateFlow`. Never holds `Context` or Android `View` types.
- Coroutine dispatchers are injected (`@IoDispatcher`, `@DefaultDispatcher`), never hardcoded.

UiState shape:

```kotlin
sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    data class Success(val days: List<DayGroup>, val filter: HistoryFilter) : HistoryUiState
    data object Empty : HistoryUiState
}
```

## Navigation

- Navigation Compose with **type-safe routes** (`@Serializable` objects/data classes).
- Each feature exposes `fun NavGraphBuilder.xxxScreen(onNavigateToYyy: (...) -> Unit)` and `fun NavController.navigateToXxx()`.
- `app` owns the `NavHost` and connects features.
- Bottom navigation: Beranda (dashboard), Riwayat (history), center FAB (add sheet), Statistik, Dompet (wallets). Settings is reached from the Dashboard top bar.

## Error handling

| Kind | Example | Handling |
|------|---------|----------|
| Validation | amount = 0 | Sealed `ValidationError` → shown inline on the field |
| Expected domain failure | delete wallet with transactions | Sealed result → dialog offering archive |
| IO / DB failure | disk full | Caught in repository, mapped to `DataError`, generic snackbar |
| Programmer error | impossible state | `error("...")` / `check()` — crash in debug |

Never swallow exceptions silently. Never catch `CancellationException` without rethrowing.

## Threading

- Room DAOs: `suspend` for writes, `Flow` for reads.
- Parsing (`core:parser`) is synchronous pure code, called on `@DefaultDispatcher`.
- `NotificationListenerService.onNotificationPosted` hands work to an injected `CoroutineScope` (application scope) immediately.

## Build logic (convention plugins)

`build-logic/convention` provides plugins so each module's `build.gradle.kts` is tiny:

| Plugin id | Applies |
|-----------|---------|
| `fintrack.android.application` | Android app config, Compose, lint, detekt, kover |
| `fintrack.android.library` | Android library config, lint, detekt, kover |
| `fintrack.android.feature` | library + Compose + Hilt + core:ui/designsystem/domain deps |
| `fintrack.android.room` | Room + KSP + schema export |
| `fintrack.android.hilt` | Hilt + KSP |
| `fintrack.jvm.library` | Pure Kotlin module config, detekt, kover |
| `fintrack.quality` | Spotless, detekt, Konsist wiring (applied by all of the above) |

All versions live in `gradle/libs.versions.toml`. No version literals in module build files.
