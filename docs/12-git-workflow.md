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

## Commit messages — Conventional Commits + doc IDs (enforced)

Every commit header **must end with the doc IDs** it relates to (ADR-014), so the owner can jump straight
from `git log` to the spec.

```
<type>(<scope>): <subject> [<ID>, <ID>]

<body: optional, short — what and why, wrapped at 72 chars>
```

| Rule | Value |
|------|-------|
| `type` | `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `perf`, `build`, `ci`, `chore`, `revert` |
| `scope` | module name without prefix: `parser`, `domain`, `database`, `transaction`, `inbox`, `designsystem`, ... or `deps`, `docs` |
| subject | imperative mood, lowercase start, no period |
| IDs | **required**, in `[...]` at the end, comma-separated, most specific first |
| header length | ≤ 100 chars |
| body | optional; keep it short. `BREAKING CHANGE:` footer if any |

Allowed IDs (see docs/README.md): `FR-xxx`, `NFR-xxx`, `BR-xxx`, `S-xx`, `ADR-xxx`, `Q-xxx`, `P<n>`, `DOC-xx`.

Which ID to use:

| Change | Reference |
|--------|-----------|
| Feature / behaviour | the `FR`/`BR` it implements (+ `S-xx` for UI) |
| Bug fix | the `FR`/`BR`/`NFR` that was violated |
| Tooling, lint, CI, build setup | roadmap phase + doc: `[P1, DOC-05]` |
| Dependency change | `[DOC-04]` |
| Docs change | the doc(s) changed: `[DOC-12]` |
| Decision change | the new ADR: `[ADR-015]` |

Examples:

```
feat(parser): add BCA QRIS debit template [FR-083]
fix(domain): deduct transfer fee from source wallet only [BR-004]
feat(transaction): add amount keypad to add sheet [FR-033, S-04]
build: add detekt with strict limits [P1, DOC-05]
build(deps): add vico chart library [DOC-04]
docs: require doc ids in commits and comments [ADR-014, DOC-12]
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
| `commit-msg` | Validates header regex (incl. doc IDs) and length ≤ 100, rejects `Co-Authored-By`, AI attribution lines, and author ≠ meQlause |
| `pre-push` | `./gradlew check` |

Header regex (extended regex, one line):

```
^(feat|fix|refactor|test|docs|style|perf|build|ci|chore|revert)(\([a-z0-9-]+\))?!?: [a-z][^][]*[^.[:space:]] \[((FR|NFR|BR|ADR|Q)-[0-9]{3}|S-[0-9]{2}|DOC-[0-9]{2}|P[0-9])(, ((FR|NFR|BR|ADR|Q)-[0-9]{3}|S-[0-9]{2}|DOC-[0-9]{2}|P[0-9]))*\]$
```

The hook should also warn (not fail) if an ID does not exist in `docs/` (`grep -rq "<ID>" docs/`).

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
- Signing: locally `keystore.properties` (git-ignored; keys `storeFile`, `storePassword`, `keyAlias`, `keyPassword`)
  (it is a Java properties file: write Windows paths with forward slashes, `D:/keys/fintrack.jks`, or doubled
  backslashes, because a single `\` is an escape and silently corrupts the path)
  makes `./gradlew assembleRelease` (APK) and `./gradlew bundleRelease` (AAB for Play) signed; without it they are
  unsigned and still build.
- `.github/workflows/release.yml` runs on a pushed tag `v*`, restores the key from secrets, builds the signed APK and
  AAB and attaches both to the GitHub Release (created with generated notes if it does not exist yet; edit the notes
  in Indonesian afterwards). Required repository secrets (Settings, Secrets and variables, Actions):
  `KEYSTORE_BASE64` (the keystore file, base64 on one line), `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
