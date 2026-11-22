# Fintrack

Fintrack is a fully offline, private personal finance tracker for Android. It is built with Kotlin and
Jetpack Compose. The specification lives in [docs/](docs/README.md).

## Git hooks

Activate the hooks once per clone (repository-local, not global):

```sh
git config core.hooksPath .githooks
```

- `pre-commit`: `./gradlew spotlessCheck detekt`
- `commit-msg`: validates the commit header format and doc IDs, rejects AI attribution
- `pre-push`: `./gradlew check`

## Main commands

| Command | Purpose |
|---------|---------|
| `./gradlew check` | Full quality gate (lint, detekt, tests, coverage, screenshots) |
| `./gradlew test` | Unit tests |
| `./gradlew spotlessApply` | Format the code |
| `./gradlew recordRoborazziDebug` | Update screenshot references |

## Documentation

- [docs/README.md](docs/README.md): index of the specification
- [docs/12-git-workflow.md](docs/12-git-workflow.md): commit and hook rules
