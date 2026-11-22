# AGENTS.md

Instructions for any AI coding agent working in this repository.

1. **Read `docs/14-ai-agent-guide.md` first.** Then `docs/README.md` for the index.
2. The documents in `docs/` are the specification. Do not invent anything they do not say — ask the owner.
3. `./gradlew check` must be green before a task is done or committed.
4. Never weaken quality rules: no `@Suppress`, no baselines, no `--no-verify`, no edits to protected files (`docs/05-coding-standards.md` §9).
5. Strict limits: functions ≤ 30 lines, files ≤ 250 lines, lines ≤ 120 chars, every name ≥ 4 characters, zero dead code.
6. Fully offline Android app: never add the INTERNET permission or any network library.
7. Test first. Fakes, not mocks.
8. Commits: Conventional Commits ending with doc IDs, e.g. `feat(parser): add BCA QRIS template [FR-083]`.
   Author `meQlause <ardialbrian@gmail.com>`, **no `Co-Authored-By`, no AI attribution**. Commit only when the owner asks.
   Comments/KDoc start with the doc ID they implement, one line: `// BR-004: fee is deducted from source only`.
9. Code, docs and commits in English; all user-facing strings in Indonesian.
10. Report back to the owner in Indonesian.
