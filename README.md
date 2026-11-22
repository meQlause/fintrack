# Fintrack

Fintrack is a fully offline, private personal finance tracker for Android. It is built with Kotlin and
Jetpack Compose. The UI is in Indonesian. The app has no network permission and no network libraries.
The specification lives in [docs/](docs/README.md).

## Requirements

- JDK 21
- Android SDK with compileSdk 37.2 (`ANDROID_HOME` must point to it, e.g. `%LOCALAPPDATA%\Android\Sdk` on Windows)
- Git Bash or any POSIX shell for the commands below

## Setup

Activate the git hooks once per clone (repository-local, not global):

```sh
git config core.hooksPath .githooks
```

- `pre-commit`: `./gradlew spotlessCheck detekt`
- `commit-msg`: validates the commit header format and doc IDs, rejects AI attribution
- `pre-push`: `./gradlew check`

## Main commands

| Command | Purpose |
|---------|---------|
| `./gradlew check` | Full quality gate (format, detekt, lint, tests, architecture, coverage, screenshots) |
| `./gradlew test` | Unit tests |
| `./gradlew spotlessApply` | Format the code |
| `./gradlew recordRoborazziDebug` | Update screenshot references (only for intended UI changes) |
| `./gradlew installDebug` | Install the debug build on a connected device |

## Documentation

- [docs/README.md](docs/README.md): index of the specification
- [docs/12-git-workflow.md](docs/12-git-workflow.md): commit and hook rules
- [docs/05-coding-standards.md](docs/05-coding-standards.md): limits and forbidden constructs enforced by `check`
