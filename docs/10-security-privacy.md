# 10 — Security & Privacy

## Privacy promise (shown in the app)

> "Data keuanganmu tidak pernah meninggalkan perangkat ini. Aplikasi ini tidak memiliki izin internet."

This promise is **technically enforced**, not just a policy.

## Offline guarantee (NFR-001, NFR-002)

- The merged manifest of the release build must not contain `android.permission.INTERNET` or
  `ACCESS_NETWORK_STATE`. Libraries that add it are forbidden. If a library's manifest adds it,
  remove with `tools:node="remove"` **and** add an ADR explaining why the library is still acceptable.
- Test: `ManifestNoInternetTest` (Konsist/Gradle task) parses `build/intermediates/merged_manifest/release/...`.
- No analytics, crash reporting, remote config, ads.
- `android:allowBackup="false"` and `android:dataExtractionRules` excluding everything — Android cloud backup
  would copy financial data off-device. Our own encrypted backup replaces it (11).

## Permissions (complete list)

| Permission | Why | When requested |
|-----------|-----|----------------|
| `BIND_NOTIFICATION_LISTENER_SERVICE` (service permission) | Notification detection | User enables in system settings |
| `POST_NOTIFICATIONS` (Android 13+) | Inbox prompts, budget alerts, backup reminder | Onboarding / first time needed |
| `USE_BIOMETRIC` | Biometric unlock | Normal permission |
| `RECEIVE_BOOT_COMPLETED` | WorkManager reschedule (added by WorkManager) | Normal permission |

`<queries>` entries list the supported bank/e-wallet package names so the app can check which are installed.
No storage permission: files go through the Storage Access Framework (`ACTION_CREATE_DOCUMENT` / `ACTION_OPEN_DOCUMENT`).

## Database encryption (NFR-003)

- Room uses SQLCipher via `SupportOpenHelperFactory(passphrase)`.
- Passphrase: 32 random bytes generated on first launch with `SecureRandom`.
- Stored encrypted: an AES-256-GCM key in **Android Keystore** (`KeyGenParameterSpec`, non-exportable) encrypts
  the passphrase; ciphertext + IV stored in a private file `files/db_key.bin`.
- Keystore key does **not** require user authentication (the app must read notifications in the background while locked).
- The PIN therefore protects the **UI**, while the Keystore protects data **at rest** against file extraction.

## PIN (FR-100, FR-103)

- 6 digits.
- Stored as `PBKDF2WithHmacSHA256`, 210,000 iterations, 16-byte random salt, 32-byte output. Store hash + salt (Base64) in DataStore.
- Comparison in constant time (`MessageDigest.isEqual`).
- Throttling: after 5 consecutive failures, lock for 30 s; each further failure doubles (60 s, 120 s …, max 15 min).
  `failedPinAttempts` and `lockedUntil` persisted so killing the app does not reset them.
- Changing or removing the PIN requires the current PIN.
- **No PIN recovery** (offline, no account). Forgetting the PIN means: clear app data and restore a backup.
  Shown clearly when setting the PIN.

## Biometrics (FR-101)

- `BiometricPrompt` with `BIOMETRIC_STRONG or BIOMETRIC_WEAK`; PIN is always the fallback (not device credential).
- Enabling biometrics requires a PIN to exist.

## App lock behaviour (FR-102)

- `ProcessLifecycleOwner` tracks background time. On foreground, if `elapsed ≥ lockTimeoutSeconds` and a PIN exists → show Lock screen.
- While locked, the notification listener keeps working and inbox prompts show **no amount** in the notification
  when a PIN is set ("Transaksi baru terdeteksi"), to avoid leaking data on the lock screen. Notification visibility: `VISIBILITY_PRIVATE`.
- `FLAG_SECURE` when "Sembunyikan dari aplikasi terbaru" is on (FR-104).

## Logging

- Only through `AppLogger` (core:common). Release build: no-op. Debug: `Log.d`, but **never** amounts, merchants, notification text, wallet names or PIN data.
- R8 rules strip `Log` calls in release as a second safety net.

## Code & build security

- Release builds: R8 full mode, minify + shrink resources.
- Signing keystore is **never** committed. Signing config reads from `keystore.properties` (git-ignored) or CI secrets.
- `.gitignore` includes: `keystore.properties`, `*.jks`, `*.keystore`, `local.properties`, `.env*`.
- Dependencies pinned; Gradle dependency verification (`verification-metadata.xml`) enabled in P2 (Q-007).
- The repository is public (ADR-013): no real personal data or secrets in git; GitHub secret scanning enabled.

## Threat model (short)

| Threat | Mitigation |
|--------|-----------|
| Someone picks up the unlocked phone | PIN/biometric lock, auto-lock timeout, hide amounts |
| Data extraction from the device storage (rooted/forensic) | SQLCipher + Keystore-protected key |
| Data leaking via cloud backup | `allowBackup=false`, data extraction rules |
| Data leaking via lock-screen notifications | Private visibility, no amounts when PIN set |
| Data leaking via network | No INTERNET permission |
| Malicious backup file on restore | Strict schema validation, size limit, version check (11) |
| Shoulder-surfing in recents | FLAG_SECURE option |
