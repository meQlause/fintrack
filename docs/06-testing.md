# 06 — Testing Strategy

## Principles

1. **Test first** for domain logic, parsers and ViewModels: write the failing test, then the minimum code, then refactor.
2. **Fakes, not mocks** (ADR-007). Fakes live in `core:testing` and implement the real interfaces.
3. **Tests run on the JVM** wherever possible (fast, no emulator). Robolectric for Android/Compose/Room.
4. **Deterministic:** inject `Clock` and dispatchers; no real time, no randomness without a seed, no `Thread.sleep`.
5. Every bug fix starts with a regression test that reproduces it.

## Test pyramid

| Layer | Module(s) | Kind | Tools | Location |
|-------|-----------|------|-------|----------|
| Model & domain | `core:model`, `core:domain` | Unit | JUnit 5, AssertK, coroutines-test, Turbine | `src/test` |
| Parser | `core:parser` | Unit, fixture-driven | JUnit 5 parameterized, AssertK | `src/test` + `src/test/resources/fixtures` |
| Data | `core:data` | Unit with fake DAOs, or integration with in-memory Room | Robolectric, Room testing | `src/test` |
| Database | `core:database` | DAO + migration tests | Robolectric, `MigrationTestHelper` | `src/test` |
| ViewModel | `feature:*` | Unit with fake repositories | JUnit 5, Turbine | `src/test` |
| Screen | `feature:*` | Compose UI interaction | Robolectric + Compose UI Test (JUnit 4) | `src/test` |
| Visual | `feature:*`, `core:designsystem`, `core:ui` | Screenshot | Roborazzi | `src/test` + `src/test/screenshots` |
| Architecture | whole repo | Konsist | Konsist + JUnit 5 | `app/src/test/kotlin/.../architecture` |
| End-to-end (few) | `app` | Instrumented smoke on device/emulator | AndroidX Test, Compose UI Test | `src/androidTest` |

## Coverage (Kover, enforced in `check`)

| Module group | Minimum line coverage | Minimum branch coverage |
|--------------|----------------------|-------------------------|
| `core:parser` | 95% | 90% |
| `core:domain`, `core:model` | 90% | 85% |
| `core:data`, `core:database`, `core:security`, `core:backup` | 80% | 70% |
| `feature:*` ViewModels | 85% | 75% |
| Overall | 75% | — |

Excluded from coverage: Hilt-generated code, `*_Factory`, `*Module`, `@Preview` functions, `R` classes, `BuildConfig`.

## Test naming

```kotlin
@Test
fun `given transfer between two wallets when saved then source decreases and target increases (BR-004)`() { ... }
```

Format: `given <state> when <action> then <outcome>` + optional rule ID. One behaviour per test.
Structure inside the test: **Arrange / Act / Assert** separated by a blank line (no comments needed).

## Fakes (core:testing)

```
core/testing/src/main/kotlin/com/meqlause/fintrack/core/testing/
├── repository/   FakeWalletRepository, FakeTransactionRepository, FakeCategoryRepository, ...
├── data/         TestData builders: aWallet(), aTransaction(), anInboxItem() with sensible defaults
├── time/         FakeClock (settable, advanceBy(duration))
├── coroutine/    MainDispatcherExtension (JUnit 5), MainDispatcherRule (JUnit 4)
└── security/     FakeKeyProvider
```

Rules for fakes:
- Backed by an in-memory `MutableStateFlow<List<T>>` so `Flow` observers react.
- Expose test hooks (e.g. `var shouldFailNextWrite: Boolean`) only when a test needs them.
- A fake must behave like the real implementation for the contract (e.g. auto-increment ids, uniqueness).
- Contract tests: an abstract `WalletRepositoryContractTest` is run against both the fake and the real implementation, so fakes stay honest.

## ViewModel test template

```kotlin
class AddTransactionViewModelTest {
    @JvmField @RegisterExtension val mainDispatcher = MainDispatcherExtension()

    private val transactionRepository = FakeTransactionRepository()
    private val clock = FakeClock(Instant.parse("2026-11-22T03:00:00Z"))

    @Test
    fun `given empty amount when save tapped then shows amount error`() = runTest {
        val viewModel = createViewModel()

        viewModel.onSaveClick()

        viewModel.uiState.test {
            assertThat(awaitItem().amountError).isEqualTo(AmountError.Required)
        }
    }
}
```

## Parser fixtures

Every supported notification format has fixtures — real notification texts with personal data anonymized.

```
core/parser/src/test/resources/fixtures/
├── bca/
│   ├── debit_qris_001.json
│   ├── credit_transfer_001.json
│   └── ignore_otp_001.json
├── gopay/
│   └── ...
```

Fixture format:

```json
{
  "description": "BCA QRIS payment",
  "packageName": "com.bca.mybca.omni.android",
  "title": "Transaksi Berhasil",
  "text": "Pembayaran QRIS Rp25.000,00 ke INDOMARET CABANG X berhasil.",
  "postedAt": "2026-11-20T10:15:00+07:00",
  "expected": {
    "kind": "TRANSACTION",
    "amount": 25000,
    "direction": "DEBIT",
    "merchant": "INDOMARET CABANG X"
  }
}
```

`expected.kind` is `TRANSACTION` or `IGNORED`. A single parameterized test loads all fixtures and asserts each.
**Adding a fixture is mandatory when adding or changing a parser.** Anonymize names, account numbers and
reference numbers (replace with `XXXX`), keep the structure exactly.

## Screenshot tests (Roborazzi)

- Each `Screen` composable: states Loading / Empty / Success / Error × light / dark.
- Reference images committed under `src/test/screenshots/`.
- `./gradlew verifyRoborazziDebug` runs in `check`; `./gradlew recordRoborazziDebug` updates references (only when the change is intended — mention it in the commit body).
- Font scale 2.0 variant for key screens (NFR-010).

## Architecture tests (Konsist) — required list

| Test | Asserts |
|------|---------|
| `ModuleDependencyTest` | feature ↛ feature, feature ↛ data/database/datastore |
| `PureKotlinTest` | no `android.*` imports in model/common/domain/parser |
| `NamingSuffixTest` | suffix table in `05` §3 |
| `NamingLengthTest` | all properties, parameters, local variables, lambda params ≥ 4 chars (except `_`, `it`) |
| `FileLengthTest` | every `.kt` file ≤ 250 lines |
| `OneTopLevelDeclarationTest` | one public top-level declaration per file, file named after it |
| `NoSuppressTest` | no `@Suppress`, `@SuppressLint`, `ktlint-disable` anywhere |
| `NoBaselineTest` | no `detekt-baseline.xml`, `lint-baseline.xml` in repo |
| `MoneyTypeTest` | money-named properties are `Long`/`Money` |
| `ViewModelTest` | ViewModels expose only `StateFlow`, no `Context` constructor params |
| `ComposableModifierTest` | public UI composables have `modifier: Modifier = Modifier` |
| `NoHardcodedDesignValuesTest` | no `Color(0x` / raw `.dp` outside designsystem |
| `DocReferenceTest` | required declarations (05 §8) have KDoc containing a doc ID |
| `ManifestNoInternetTest` | merged release manifest has no `INTERNET` permission (NFR-001) |
| `ForbiddenDependenciesTest` | no MockK/Mockito/Firebase/OkHttp in any configuration |

## Commands

| Command | What |
|---------|------|
| `./gradlew test` | all JVM unit + Robolectric tests |
| `./gradlew :core:parser:test` | one module |
| `./gradlew koverVerify` | coverage thresholds |
| `./gradlew verifyRoborazziDebug` | screenshot comparison |
| `./gradlew check` | **everything** (format, detekt, lint, tests, coverage, screenshots, konsist, buildHealth) |
| `./gradlew connectedDebugAndroidTest` | device smoke tests (manual / CI optional) |

## Definition of "tested"

A feature is tested when:
- every FR acceptance criterion has a test mentioning the FR id in its name or KDoc,
- every BR it implements has a test,
- all its screens have screenshot tests for all states,
- coverage thresholds pass.
