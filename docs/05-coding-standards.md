# 05 — Coding Standards (Strict)

The code is written mostly by AI agents. These rules are therefore **machine-enforced**. A rule that is
only written here but not checked by a tool is a bug in the tooling — report it.

The single gate: **`./gradlew check` must pass.** Nothing is done, committed or pushed while it fails.

---

## 1. Formatting — Spotless + ktlint

- Style: `ktlint_official` code style (official Kotlin coding conventions), `.editorconfig` at repo root.
- Max line length: **120**.
- Indent: 4 spaces. No tabs. LF line endings. Final newline required. UTF-8.
- Trailing commas: required on multi-line declarations and call sites.
- Imports: no wildcard imports; ordered lexicographically; unused imports removed.
- Commands:
  - `./gradlew spotlessApply` — fix formatting.
  - `./gradlew spotlessCheck` — part of `check`.

`.editorconfig` essentials:

```ini
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
indent_style = space
indent_size = 4
trim_trailing_whitespace = true
max_line_length = 120

[*.{kt,kts}]
ktlint_code_style = ktlint_official
ktlint_function_naming_ignore_when_annotated_with = Composable
ij_kotlin_allow_trailing_comma = true
ij_kotlin_allow_trailing_comma_on_call_site = true
ij_kotlin_name_count_to_use_star_import = 2147483647
ij_kotlin_name_count_to_use_star_import_for_members = 2147483647

[*.{xml,json,yml,yaml,toml,md}]
indent_size = 2
```

## 2. Hard limits

| Rule | Limit | Tool / rule |
|------|-------|-------------|
| Function length | **30 lines** (body, excluding blank lines and comments) | detekt `LongMethod` |
| File length | **250 lines** | Konsist test `FileLengthTest` |
| Class length | **200 lines** | detekt `LargeClass` |
| Line length | **120 chars** | ktlint + detekt `MaxLineLength` |
| Cyclomatic complexity | **10** per function | detekt `CyclomaticComplexMethod` |
| Cognitive complexity | **15** per function | detekt `CognitiveComplexMethod` |
| Nesting depth | **3** | detekt `NestedBlockDepth` |
| Function parameters | **5** (Composables: **8**, because of `modifier` + callbacks) | detekt `LongParameterList` (+ ignoreAnnotated Composable at 8 via Compose rules) |
| Constructor parameters | **7** | detekt `LongParameterList.constructorThreshold` |
| Functions per file / class | **15** | detekt `TooManyFunctions` |
| Return statements | **2** per function (guard clauses excluded) | detekt `ReturnCount` |
| Conditions in one expression | **3** | detekt `ComplexCondition` |
| Lambda nesting | **3** | detekt `NestedScopeFunctions` |

If a limit is hit: **split the code** (extract function, extract class, extract composable). Never suppress.

## 3. Naming

| Element | Convention | Minimum length | Example |
|---------|-----------|----------------|---------|
| Variables, properties, parameters | camelCase | **4 chars** | `amount`, `walletName`, `index` |
| Functions | camelCase verb phrase | 4 chars | `observeBalances()` |
| Composables | PascalCase noun | — | `AmountKeypad()` |
| Classes / interfaces | PascalCase noun | — | `TransactionRepository` |
| Constants (`const val`, top-level/companion) | UPPER_SNAKE_CASE | — | `MAX_NOTE_LENGTH` |
| Enum entries | UPPER_SNAKE_CASE | — | `CREDIT_CARD` |
| Packages | lowercase, no underscores | — | `feature.transaction.edit` |
| Test functions | backticked sentence | — | `` `given zero amount when saving then returns error`() `` |
| Boolean | `is`/`has`/`can`/`should` prefix | — | `isArchived`, `hasPin` |

**Minimum 4 characters, no exceptions.** Examples of forbidden → required:

| Forbidden | Use instead |
|-----------|-------------|
| `id` | `walletId`, `transactionId`, `itemId` |
| `i`, `j` | `index`, `rowIndex` |
| `db` | `database` |
| `vm` | `viewModel` |
| `ctx` | `context` |
| `tx`, `trx` | `transaction` |
| `e`, `ex` | `error`, `exception` |
| `x`, `y` | `offsetX`, `positionY` |
| `amt` | `amount` |
| `cat` | `category` |
| `_` | allowed only for intentionally unused destructured/lambda params |

`it` is allowed only in single-line lambdas. Multi-line lambdas must name the parameter (≥ 4 chars).

Enforcement: detekt `VariableMinLength(minimumVariableNameLength = 4)`, `FunctionMinLength(4)`,
plus Konsist `NamingTest` for parameters, lambda parameters and properties (detekt does not cover all).

Suffix conventions (Konsist-enforced):

| Kind | Suffix | Location |
|------|--------|----------|
| ViewModel | `ViewModel` | feature module |
| UI state | `UiState` | next to its ViewModel |
| Stateful screen entry | `Route` | feature module |
| Stateless screen | `Screen` | feature module |
| Use case | `UseCase` | `core.domain.usecase` |
| Repository interface | `Repository` | `core.domain.repository` |
| Repository implementation | `Local` prefix + `Repository` (`LocalWalletRepository`) | `core.data.repository` |
| Room entity | `Entity` | `core.database.entity` |
| DAO | `Dao` | `core.database.dao` |
| Fake (test double) | `Fake` prefix (`FakeWalletRepository`) | `core.testing` |
| Notification parser | `Parser` | `core.parser.bank` / `core.parser.ewallet` |
| Hilt module | `Module` | `di` package |

## 4. Dead code (zero tolerance)

| What | Tool |
|------|------|
| Unused private functions, properties, classes | detekt `UnusedPrivateFunction` / `UnusedPrivateProperty` / `UnusedPrivateClass` |
| Unused parameters | detekt `UnusedParameter` |
| Unused imports | ktlint + detekt `UnusedImports` |
| Unused variables | detekt `UnusedVariable` + compiler warnings as errors |
| Unused public/internal declarations across modules | Qodana `UnusedSymbol` inspection (CI) |
| Unused resources (strings, drawables) | Android Lint `UnusedResources` (error) |
| Unused dependencies | Dependency Analysis Plugin (`buildHealth` fails on issues) |
| Commented-out code | detekt `CommentedOutCode` (custom/Konsist if unavailable) |

Code written "for later" is dead code. Do not add it.

> Note: detekt rule names changed between 1.x and 2.x (e.g. `UnusedPrivateMember` was split). When
> configuring, verify each rule name against the installed detekt version; a misspelled rule silently does nothing.
> A Konsist test asserts the detekt config loads with `config.validation = true` (unknown keys fail the build).

## 5. Forbidden constructs

| Forbidden | Why | Rule |
|-----------|-----|------|
| `@Suppress` / `@SuppressLint` / `// ktlint-disable` / `@file:Suppress` | AI must fix, not hide | detekt `ForbiddenSuppress` + Konsist |
| detekt/lint **baselines** | Same | no baseline files allowed (Konsist checks repo) |
| `!!` | NPE risk | detekt `UnsafeCallOnNullableType` |
| `lateinit` outside DI/test classes | hidden nullability | Konsist |
| `GlobalScope` | leaks | detekt `GlobalCoroutineUsage` |
| `runBlocking` in main code | blocks threads | detekt `ForbiddenMethodCall` |
| `println`, `System.out`, `printStackTrace` | no logging to stdout | detekt `ForbiddenMethodCall` |
| `Log.*` in release paths | privacy (financial data) | Lint + Konsist: only via `core:common` `AppLogger` which is a no-op in release and never logs amounts or notification text |
| `Thread.sleep` | | `ForbiddenMethodCall` |
| `System.currentTimeMillis()`, `Instant.now()`, `LocalDate.now()` without Clock | testability | `ForbiddenMethodCall` |
| `Double`/`Float` for money | precision | Konsist: properties named `amount*`/`*Amount`/`balance*` must be `Long` or `Money` |
| Magic numbers | readability | detekt `MagicNumber` (ignore -1, 0, 1, 2; ignore in tests, Compose `dp`/`sp` via design tokens instead) |
| Hardcoded UI strings | localization | Lint `HardcodedText` + Konsist: `Text("...")` with a literal is forbidden; use `stringResource` |
| Hardcoded colors / dp in feature modules | design consistency | Konsist: `Color(0x...)` and raw `.dp` literals only allowed in `core:designsystem` |
| `TODO` / `FIXME` without issue ref | debt tracking | detekt `ForbiddenComment` (allowed pattern: `TODO(#123):`) |
| Wildcard imports | | ktlint + detekt `WildcardImport` |
| `catch (e: Exception)` that swallows | | detekt `SwallowedException`, `TooGenericExceptionCaught` |
| Mutable public state (`var` in public API, exposed `MutableStateFlow`) | encapsulation | Konsist |
| `object` singletons holding state | testability | Konsist (use Hilt `@Singleton`) |
| `fallbackToDestructiveMigration` | data loss | `ForbiddenMethodCall` |

## 6. Kotlin style

- Prefer `val` over `var`; immutable collections (`List`, `kotlinx.collections.immutable` in UI state).
- Explicit return types on all public/internal functions. Explicit API mode (`explicitApi()`) in pure Kotlin `core:*` modules.
- Visibility: as small as possible (`private` > `internal` > `public`).
- Expressions over statements where readable (`when` as expression, early returns).
- `when` on sealed types/enums must be exhaustive with **no `else`** branch.
- Data classes for state; sealed interfaces for alternatives.
- No extension functions on `Any`, `String` or other broad types in public API — put them in `internal` scope.
- KDoc on every public declaration in `core:*` modules (detekt `UndocumentedPublicClass`, `UndocumentedPublicFunction` active for `core`).
- Comments explain **why**, not what.

## 7. Compose rules

- Every composable that emits UI takes `modifier: Modifier = Modifier` as the **first optional** parameter and applies it to the root.
- Stateless by default; state hoisted to `Route`/ViewModel.
- No ViewModel parameters below the `Route` level.
- UI state classes are `@Immutable`/`@Stable` or use immutable collections.
- `remember` for computed values; `derivedStateOf` for state derived from frequently changing state.
- Every `Screen` and every reusable component has at least one `@Preview` (light + dark), using `PreviewParameterProvider` for data — previews double as Roborazzi screenshot tests.
- Lists use `LazyColumn` with stable `key`s (the entity id).
- Strings via `stringResource`, plurals via `pluralStringResource`.
- Colors, spacing, shapes, typography come from `FintrackTheme` tokens only (`08-design-system.md`).
- Compose Rules for detekt are all enabled at their defaults.

## 8. Comments & documentation

- Public API of `core:*`: KDoc required.
- Reference requirement/rule IDs in KDoc where behaviour comes from a rule: `/** Applies BR-010. */`
- No commented-out code. No author tags, no dates in comments (git has that).

## 9. Protected files (AI must not modify without owner approval)

- `config/detekt/detekt.yml`
- `.editorconfig`
- `build-logic/**` quality configuration (limits, plugin setup)
- `gradle/libs.versions.toml` (adding/upgrading dependencies)
- `konsist` tests under `app/src/test/.../architecture/` (may add tests, not weaken them)
- `.github/workflows/**`
- `.githooks/**`
- `docs/16-decisions.md`

Loosening any limit (e.g. 30 → 40 lines) is a decision → ADR + owner approval.
