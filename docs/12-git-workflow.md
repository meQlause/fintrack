# 12 — Git Workflow

## Repository

- Host: GitHub, account **meQlause**, repository **public** (ADR-013): never commit real personal data or secrets.
- Default branch: `main` (protected: no direct force-push; CI must pass).

## Author identity (STRICT)

Every commit must be authored **and** committed by the owner:

```
user.name  = meQlause
user.email = ardialbrian@gmail.com
```

Set locally in this repository:

```bash
git config user.name "meQlause"
git config user.email "ardialbrian@gmail.com"
```

**Forbidden in commit messages** (rejected by the `commit-msg` hook):
- `Co-Authored-By:` trailers of any kind (no AI co-author).
- "Generated with Claude", "Claude Code", "🤖", or any AI attribution line.
- `Signed-off-by` from any identity other than the owner.

AI agents: never pass `--author`, never add trailers, never change `git config` identity, never use `--no-verify`.

## Branches

| Branch | Purpose |
|--------|---------|
| `main` | Always releasable, always green |
| `feat/<short-name>` | New feature, e.g. `feat/inbox-swipe` |
| `fix/<short-name>` | Bug fix |
| `chore/<short-name>`, `docs/<short-name>`, `build/<short-name>` | Other |

Small changes may be committed directly to `main` by the owner. AI agents work on a branch unless told otherwise.

## Commit messages — Conventional Commits (enforced)

```
<type>(<scope>): <subject>

<body: what and why, wrapped at 72 chars>

<footer: Refs: FR-030, BR-001>
```

| Rule | Value |
|------|-------|
| `type` | `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `perf`, `build`, `ci`, `chore`, `revert` |
| `scope` | module name without prefix: `parser`, `domain`, `database`, `transaction`, `inbox`, `designsystem`, ... or `deps`, `docs` |
| subject | imperative mood, lowercase start, no period, ≤ 72 chars total header |
| body | optional, explains **why** |
| footer | `Refs:` requirement IDs when applicable; `BREAKING CHANGE:` if any |

Examples:

```
feat(parser): add BCA QRIS debit template

Refs: FR-083
```

```
fix(domain): exclude transfer fees from transfer amount (BR-004)
```

```
docs: add product and engineering specification
```

## Commit rules

1. **Each commit passes `./gradlew check`.** No "WIP" commits on `main`.
2. One logical change per commit. Tests and the code they test go in the same commit.
3. No generated files (build outputs), no secrets, no IDE files except shared code style (`.idea/codeStyles` optional).
4. Screenshot reference updates are mentioned in the body: "Updated screenshots: DashboardScreen (dark)".
5. Never rewrite pushed history on `main`.

## Hooks (`.githooks/`, activated with `git config core.hooksPath .githooks`)

| Hook | Does |
|------|------|
| `pre-commit` | `./gradlew spotlessCheck detekt` on changed modules (fast) |
| `commit-msg` | Validates Conventional Commit header regex, rejects `Co-Authored-By`, AI attribution lines, and author ≠ meQlause |
| `pre-push` | `./gradlew check` |

Header regex:

```
^(feat|fix|refactor|test|docs|style|perf|build|ci|chore|revert)(\([a-z0-9-]+\))?!?: [a-z].{0,70}[^.]$
```

## CI (GitHub Actions, `.github/workflows/ci.yml`)

On every push and PR:
1. Checkout, JDK 21, Gradle cache.
2. `./gradlew check` (format, detekt, lint, Konsist, unit + Robolectric + screenshot tests, Kover, buildHealth).
3. Qodana (dead code across modules).
4. `./gradlew assembleRelease` (unsigned) to verify R8 and the no-INTERNET manifest test.
5. Upload reports (detekt, lint, test, kover) as artifacts on failure.

## Releases

- Version: `versionName` = SemVer (`1.0.0`), `versionCode` = `major*10000 + minor*100 + patch`.
- Tag: `v1.0.0` on `main`. Release notes in Indonesian (for friends) in GitHub Releases, with the signed APK attached
  (public repository, ADR-013).
