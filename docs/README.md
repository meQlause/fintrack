# Documentation Index

This folder is the **single source of truth** for the Financial Tracker app.
Every decision, rule and requirement lives here. Code must follow these documents.
If code and docs disagree, the docs win — or the docs must be updated first (with owner approval).

> **For AI agents:** read `14-ai-agent-guide.md` first, then the documents relevant to your task.
> Never guess. If something is not specified here, stop and ask the owner.

## Language convention

- All documentation, code, identifiers, comments and commit messages are in **English**.
- All **user-facing text** (UI strings) is in **Indonesian** (Bahasa Indonesia).
- `15-glossary.md` maps Indonesian UI terms to English code terms.

## Reading order

| # | File | What it answers | Read when |
|---|------|-----------------|-----------|
| 00 | [00-vision.md](00-vision.md) | Why the app exists, who it is for, what it is NOT | Always, once |
| 01 | [01-requirements.md](01-requirements.md) | Functional (FR-xxx) and non-functional (NFR-xxx) requirements | Before any feature work |
| 02 | [02-domain-model.md](02-domain-model.md) | Entities, fields, business rules, database schema | Touching data or logic |
| 03 | [03-architecture.md](03-architecture.md) | Modules, layers, dependency rules, patterns | Before creating any file |
| 04 | [04-tech-stack.md](04-tech-stack.md) | Libraries and tools, and why | Adding a dependency |
| 05 | [05-coding-standards.md](05-coding-standards.md) | Formatter, linter, strict limits, naming | Always, before writing code |
| 06 | [06-testing.md](06-testing.md) | Test strategy, tools, coverage, fixtures | Always, before writing code |
| 07 | [07-notification-detection.md](07-notification-detection.md) | Reading bank/e-wallet notifications offline | Parser / inbox work |
| 08 | [08-design-system.md](08-design-system.md) | Colors, typography, spacing, components | Any UI work |
| 09 | [09-screens.md](09-screens.md) | Every screen, its states and behaviour | Any UI work |
| 10 | [10-security-privacy.md](10-security-privacy.md) | Offline guarantee, encryption, PIN, biometrics | Security / settings work |
| 11 | [11-backup-export.md](11-backup-export.md) | Backup file format, restore, CSV export | Backup work |
| 12 | [12-git-workflow.md](12-git-workflow.md) | Branches, commit format, author identity | Before every commit |
| 13 | [13-roadmap.md](13-roadmap.md) | Phases with exit criteria (checkboxes) | Picking the next task |
| 14 | [14-ai-agent-guide.md](14-ai-agent-guide.md) | Step-by-step rules for AI agents | **First**, if you are an AI |
| 15 | [15-glossary.md](15-glossary.md) | Terms, Indonesian ↔ English mapping | When a word is unclear |
| 16 | [16-decisions.md](16-decisions.md) | Architecture Decision Records (ADR-xxx) | Before questioning a decision |
| 17 | [17-open-questions.md](17-open-questions.md) | Things not yet decided | Before assuming anything |

## Identifier conventions used across docs

- `FR-xxx` — functional requirement (01-requirements.md)
- `NFR-xxx` — non-functional requirement (01-requirements.md)
- `BR-xxx` — business rule (02-domain-model.md)
- `ADR-xxx` — architecture decision (16-decisions.md)
- `Q-xxx` — open question (17-open-questions.md)
- `P<n>` — roadmap phase (13-roadmap.md)

Code, tests and commits should reference these IDs where relevant, e.g. a test named
`given transfer when saved then both wallet balances change (BR-004)`.

## Changing documentation

1. Docs changes go in their own commit with type `docs:`.
2. Changing a decision (ADR) requires owner approval. Add a new ADR that supersedes the old one; never delete history.
3. Keep requirement IDs stable. Removed requirements are marked `~~deprecated~~`, not renumbered.
