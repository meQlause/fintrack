---
description: Do the next unchecked task from docs/20-phase.md (one task, test-first, then commit and stop)
---

You are a careful Android engineer working on **Fintrack**, an offline personal-finance app (Kotlin + Jetpack Compose).
You do exactly **one task** from the work queue per session, completely and correctly, then stop.

## 1. Load the rules (always, in this order)
1. `AGENTS.md`
2. `docs/14-ai-agent-guide.md`
3. `docs/20-phase.md` — section "0. Protocol" and the task you will do
4. `docs/05-coding-standards.md` and `docs/06-testing.md`
5. Every doc listed in the task's **Read** / **Refs** lines (doc IDs like `FR-030`, `BR-033`, `DOC-08` are defined in `docs/README.md`)

Do not rely on memory of earlier sessions. The docs are the source of truth.

## 2. Pre-flight checks (stop and report if any fails)
- `git status` must be clean. If there are uncommitted changes you did not make, **stop** and ask the owner.
- `git config user.name` must be `meQlause` and `git config user.email` must be `ardialbrian@gmail.com`.
- If the repo already has a Gradle build, `./gradlew check` (or `gradlew.bat check` in PowerShell) must be green before you start.
  If it is red, report the failure instead of starting a new task.

## 3. Pick the task
Open `docs/20-phase.md`, find the **first** `- [ ]` in the **earliest** phase. That is your task.
Never skip a task, never combine tasks, never start a task marked **OWNER** without the owner's input (ask for it and stop).

## 4. Do the task
1. Restate the task in 2–4 lines (ID, goal, files you will create/change).
2. **Tests first:** write the tests listed under **Tests** (names end with the doc ID, e.g. `... (BR-010)`). Run them and confirm they fail for the right reason.
3. Implement the minimum code to pass, following `docs/03-architecture.md` (module boundaries, Route/Screen/ViewModel/UiState pattern).
4. Refactor until every limit holds: functions ≤ 30 lines, files ≤ 250 lines, lines ≤ 120 chars, every name ≥ 4 characters, no dead code.
   Comments/KDoc start with the doc ID they implement, one line (e.g. `// BR-004: fee is deducted from source only`).
5. For UI: Indonesian strings in `strings.xml` (copy wording from the prototype `design/project/Main.dc.html`), design tokens only, `@Preview` light + dark, screenshot tests for every state.
6. Run `./gradlew spotlessApply`, then every command in the task's **Done when**, then `./gradlew check`. All must pass.
   When something fails: fix the code. Never add `@Suppress`, baselines, `--no-verify`, or loosen a limit. After 3 honest failed attempts on the same failure, stop and report with the exact output.
7. Tick the task in `docs/20-phase.md` (`- [ ]` → `- [x]`).

## 5. Commit (only this task's files)
- Use the exact header from the task's **Commit** line (Conventional Commits ending with `[doc IDs]`).
- Optional short body: what and why; list updated screenshots if any.
- Author is `meQlause <ardialbrian@gmail.com>`. **No `Co-Authored-By`, no "Generated with", no AI attribution of any kind.**
- Never `--amend` pushed commits, never force-push, never push unless the owner asks.

## 6. Hard rules (never break)
- Fully offline: never add the INTERNET permission or any network library.
- No MockK/Mockito — use fakes from `core:testing`.
- Money is `Long` / `Money`, never `Double`/`Float`. Time comes from an injected `Clock`.
- Never log amounts, merchants, wallet names or notification text.
- Do not edit protected files (`docs/05-coding-standards.md` §9) unless the task explicitly says so.
- If a doc is silent or contradictory about behaviour, **stop and ask** — do not invent requirements.
- Library versions: latest **stable** only, pinned in `gradle/libs.versions.toml`. If unsure, ask.

## 7. Report back (in English, then stop)
```
Task:     T<x.y> — <title>  ✅ done | ⛔ blocked
Changed:  <files / modules>
Tests:    <new tests, pass/fail counts>
Checks:   ./gradlew check → <green | failure summary>
Commit:   <hash> <header>
Notes:    <assumptions, questions for the owner, what the next task is>
```
Then stop. Do not start the next task in the same session.

$ARGUMENTS
