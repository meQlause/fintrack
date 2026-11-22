# 14 — AI Agent Guide

You are an AI agent writing code for this repository. Follow this guide **exactly**. It is written to be
usable by small models: when a step says "do X", do X; when something is not covered, **stop and ask**.

## Golden rules

1. **Docs are the spec.** Read the relevant docs before coding. Do not invent requirements, screens, fields or libraries.
2. **Changed modules green locally, full `check` green in CI.** Before every commit run `./gradlew :<module>:check` for
   each module you changed (and `:app:testDebugUnitTest` for the architecture tests). CI runs `./gradlew check` on push;
   a task is done only when that CI run is green.
3. **Never weaken the rules.** Do not edit protected files (05 §9), do not add `@Suppress`, baselines, `--no-verify`, or lower limits/coverage.
4. **Test first.** Write a failing test, then the minimum code to pass it, then refactor.
5. **Small steps.** One exit criterion (13) or one requirement at a time.
6. **Ask when unsure.** If a doc is ambiguous, contradictory or silent, stop and ask the owner. Add the question to `17-open-questions.md` if asked to.
7. **Commit identity:** author `meQlause <ardialbrian@gmail.com>`, **no Co-Authored-By, no AI attribution** (12).
   Commit header ends with doc IDs: `feat(parser): add BCA QRIS template [FR-083]`.
8. **Comments start with a doc ID** and stay one line: `// BR-004: fee is deducted from source only` (05 §8).
9. **Offline only.** Never add networking code or libraries.

## Before starting a task

1. Read `20-phase.md` and take the **first unchecked task** of the earliest phase. That is your task unless the owner said otherwise.
   (`13-roadmap.md` is only the high-level summary.)
2. Read the docs referenced by that criterion (requirements → domain → architecture → screens).
3. Check `17-open-questions.md`: if your task depends on an open question, stop and ask.
4. Identify the module(s) you will touch (03 → Module list). If a new module seems needed, stop and ask.

## Implementation loop

```
1. Write/extend a test that expresses the requirement (name contains FR/BR id).
2. Run the module tests → confirm it FAILS for the right reason.
3. Write the minimum production code to pass.
4. Run the module tests → PASS.
5. Refactor: keep functions ≤ 30 lines, files ≤ 250 lines, names ≥ 4 chars.
6. ./gradlew spotlessApply
7. ./gradlew :<changed-module>:check  (+ :app:testDebugUnitTest); CI runs the full check after push
8. Fix every failure by changing the code (never the rules).
9. Tick the roadmap checkbox if the criterion is fully done.
10. Commit (12) — only when the owner asked you to commit.
```

## When a limit is hit — how to fix (not suppress)

| Failure | Fix |
|---------|-----|
| `LongMethod` (> 30 lines) | Extract private functions with descriptive names; for Composables, extract child composables into `component/` |
| File > 250 lines | Split by responsibility into new files (one public declaration per file) |
| `LongParameterList` | Group parameters into a data class (`XxxParams`, `XxxCallbacks` for composable lambdas) |
| `CyclomaticComplexMethod` | Replace `if` chains with `when` over a sealed type, or a lookup map; extract branches |
| `NestedBlockDepth` | Early returns (guard clauses), extract inner block into a function |
| `VariableMinLength` / Konsist naming | Rename to a descriptive name ≥ 4 chars (see 05 §3 table) |
| `MagicNumber` | Named `private const val` with a meaningful name, or a design token |
| `UnusedPrivate*` / dead code | Delete it. Do not keep "for later" |
| `UnsafeCallOnNullableType` (`!!`) | Use `?.let`, `?:` with a sensible default or `error("why")`, or make the type non-null upstream |
| `TooGenericExceptionCaught` | Catch the specific exception type the API throws |
| Hardcoded string | Add to `strings.xml` (Indonesian) and use `stringResource` |
| Hardcoded color/dp in feature | Use `FintrackTheme.colors/spacing/shapes` tokens; if a token is missing, ask before adding one |
| Coverage below threshold | Add tests for untested branches; do not exclude files |
| Screenshot diff | If the change is intended, `recordRoborazziDebug` and mention it in the commit body; otherwise fix the UI |

## How-to recipes

### Add a new use case
1. `core/domain/.../usecase/XxxUseCase.kt`, `class XxxUseCase @Inject constructor(...)` with `operator fun invoke`.
2. Expected failures → sealed `XxxResult` in the same package (separate file).
3. Test in `core/domain/src/test/.../usecase/XxxUseCaseTest.kt` using fakes from `core:testing`.

### Add a repository method
1. Add to the interface in `core:domain/repository`.
2. Implement in `core:data/repository/LocalXxxRepository`.
3. Implement in `core:testing` `FakeXxxRepository`.
4. Extend the contract test so both implementations are verified.

### Add a screen
1. Check the screen spec in `09-screens.md` (states, actions, strings).
2. Create `XxxUiState.kt` (sealed interface with all states from the spec).
3. Create `XxxViewModel.kt` + `XxxViewModelTest.kt` (test first).
4. Create stateless `XxxScreen.kt` with `@Preview`s for every state, light + dark.
5. Create `XxxRoute.kt` connecting the ViewModel.
6. Add route + `NavGraphBuilder.xxxScreen()` in the feature's `navigation/`.
7. Wire it in `app` NavHost.
8. Screenshot tests for every state; Compose UI test for the main interaction.

### Add a notification parser
Follow `07-notification-detection.md` → "Steps to add a new app". Fixtures first.

### Change the database schema
1. Change the entity, bump the Room version.
2. Write a `Migration(n, n+1)` and a migration test with `MigrationTestHelper`.
3. Commit the new exported schema JSON.
4. Update `02-domain-model.md` and the backup DTOs/validator (11) in the same change.

## Things you must never do

- Add `INTERNET` permission or any networking library.
- Use MockK/Mockito.
- Use `Double`/`Float` for money.
- Call `Instant.now()` / `System.currentTimeMillis()` directly.
- Log amounts, merchant names, wallet names or notification text.
- Edit protected files (05 §9) or lower thresholds.
- Use `@Suppress`, baselines, `--no-verify`, `git commit --amend` on pushed commits, force-push to `main`.
- Add `Co-Authored-By` or AI attribution to commits.
- Commit secrets (`keystore.properties`, `*.jks`, `local.properties`).
- Leave TODOs without an issue reference, commented-out code, or unused code.
- Mark a task done with failing or skipped tests. If you cannot make something pass, report it honestly with the output.

## Definition of Done (per task)

- [ ] Behaviour matches the referenced FR/BR/screen spec exactly.
- [ ] KDoc/comments carry the doc IDs (05 §8); commit header ends with `[IDs]` (12).
- [ ] Tests written first, named with IDs, all passing.
- [ ] Screens: all states previewed and screenshot-tested in light + dark.
- [ ] Changed modules' `check` green locally; CI `check` green after push.
- [ ] No new open questions left unasked.
- [ ] Docs updated if behaviour or structure changed (with owner approval for decisions).
- [ ] Roadmap checkbox ticked (if a criterion was completed).
- [ ] Commit follows 12 (only when asked to commit).

## Reporting back to the owner

End each task with a short summary in English:
- what was done (requirement IDs),
- which files/modules changed,
- test/check result (paste the failing output if anything failed),
- any question or follow-up.
