# Google Play Console - declarations (draft, ready to paste)

Status: draft prepared 2026-11-24 from the repo docs and code. Items marked **verify in Play Console** could not
be confirmed against a live console. Sources fetched on 2026-11-24 (summaries, re-check before submitting):

- Data safety form: https://support.google.com/googleplay/android-developer/answer/10787469
- Prominent disclosure and consent (User Data policy): https://support.google.com/googleplay/android-developer/answer/11150561
- Target API level requirements: https://support.google.com/googleplay/android-developer/answer/11926878

Privacy policy URL to enter: `https://github.com/meQlause/fintrack/blob/main/docs/play-store/privacy-policy.md`
(works only once the file is pushed to the public repo; `ardialbrian@gmail.com` in it must be filled first).

## 1. Data safety form

Google's definition: "collect" means transmitting data off the user's device; data processed only on the device
and never transmitted is not "collected" and need not be declared. "Share" means transferring collected data to
a third party. Fintrack has no INTERNET permission (`app/src/main/AndroidManifest.xml`, `docs/10-security-privacy.md`),
so nothing is transmitted.

| Console question | Answer | Justification |
|---|---|---|
| Does your app collect or share any of the required user data types? | **No** | All data is processed and stored on-device only; nothing is transmitted (no INTERNET permission, no SDK that sends data: NFR-001, NFR-002). |
| Is all of the user data collected by your app encrypted in transit? | Not shown when "No data collected" (**verify in Play Console**) | No data is in transit. |
| Do you provide a way for users to request that their data be deleted? | Not shown when "No data collected" (**verify in Play Console**) | If asked: uninstall or clear app data; the developer holds no copy. |
| Privacy policy URL | the URL above | Required even when no data is collected. |

If the console forces a data-type grid anyway, leave every type unticked: financial info (transactions,
wallets), SMS/notification-derived info, and app info are on-device only. Do not tick "Optional" or "Processed
ephemerally": those apply to data that is transmitted.

Free-text justification (if a field exists): "Fintrack processes all data locally on the device. The app declares
no internet permission and contains no analytics, advertising or crash-reporting SDKs. Notification text from
user-selected banking apps is parsed on-device and never transmitted."

## 2. App access

All functionality is available without login, account or special credentials. Select: "All functionality in my
app is available without special access". Optional PIN lock is user-created; a reviewer can skip it (onboarding
offers "Nanti saja").

Reviewer note: the notification-detection feature needs a real or simulated banking notification from a
supported app (for example com.bca, com.gojek.gopay, id.dana, listed in `docs/07-notification-detection.md`).
Give the reviewer a short demo video (see section 6) because they cannot easily receive real bank notifications.

## 3. Ads

"No, my app does not contain ads." (No ad SDK; NFR-002, `docs/00-vision.md` non-goals.)

## 4. Content rating (IARC questionnaire hints)

- Category: Utility / Productivity / Finance (**verify in Play Console**).
- Violence, sexual content, profanity, controlled substances, gambling, user-generated content shared between
  users, location sharing, user-to-user communication, digital purchases: **No** to all.
- No simulated gambling or real-money gaming; the app moves no money.
- Expected result: Everyone / PEGI 3 / equivalent (**verify in Play Console**).

## 5. Target audience and content

- Target age group: **18 and over** (or 13+; pick 18+ for a finance tracker; **verify in Play Console**).
- Not designed for children; not in the Families program. Select "No" for "appeals to children".

## 6. Financial features declaration

Play Console has a "Financial features" declaration (**verify in Play Console** for the exact option names).
Intended answer: the app is a **personal finance management / tracking** tool only. It does not offer loans,
lending, credit, payments, money transfer, banking services, trading, crypto, or insurance. The loan and
instalment features (`docs/18-credit-installments-loans.md`) are a private ledger the user types in; the app
neither lends nor collects repayments. Investment tracking (ADR-017, `docs/19-investments.md`) is a manual value
log with no trading and no live prices. If the console lists "Personal loans" or "Investment" categories, the
app does not originate or facilitate them; answer accordingly. Do not claim bank affiliation: the app is not
endorsed by BCA, GoPay, DANA or any listed provider. Keep their names in the store listing to a factual
"works with notifications from ..." and avoid their logos.

## 7. NotificationListenerService and Google Play policy

`core/notification/src/main/AndroidManifest.xml` declares `FintrackNotificationListener` with
`BIND_NOTIFICATION_LISTENER_SERVICE`. The Play pages fetched do not mention NotificationListenerService by name
(**verify in Play Console**: look for a permissions declaration form; none was confirmed). Notification content
from banking apps is personal and sensitive financial data, so the User Data policy applies:

- **Prominent disclosure and consent**, in-app, immediately before requesting access (not only in the store
  listing or privacy policy). Requirements from the fetched page: disclose why (core purpose), what data is
  accessed, and how it is used; offer at least two choices (a clear consent such as "Setuju" and a decline that
  still allows consenting later); wording clear at a roughly 13-year-old reading level; do not style as system UI.
  Wording such as "Allow access" or "Got it" alone is discouraged as the consent button.
- **Privacy policy** must describe this access (done: section 3 of `privacy-policy.md`).
- **Data handling:** data accessed must be used only for the disclosed purpose (suggesting transactions the user
  confirms) and not transmitted. This matches `docs/07-notification-detection.md` (Privacy guarantees).
- If the console asks for a video, show: opening the app, the disclosure, consenting, declining, and a detected
  notification appearing in the Inbox.

Suggested disclosure text (Indonesian, shown before the "Aktifkan" button opens the system settings):

> **Izinkan Fintrack membaca notifikasi?**
> Agar transaksi tercatat otomatis, Fintrack perlu membaca notifikasi dari aplikasi bank dan e-wallet yang kamu
> pilih (judul dan isi pesan: jumlah, pengirim/penerima, waktu). Notifikasi dari aplikasi lain tidak diproses.
> Semua dibaca dan diproses di HP-mu saja; tidak ada yang dikirim ke internet, dan transaksi baru dicatat
> setelah kamu konfirmasi. Kamu bisa mematikan akses ini kapan saja di Pengaturan Android.
> Tombol: **Setuju** / **Tidak sekarang**

## 8. Other declarations to expect

- **Permissions in the merged manifest** (from `app/build/intermediates/merged_manifest/.../AndroidManifest.xml`):
  `POST_NOTIFICATIONS`, `USE_BIOMETRIC`, `USE_FINGERPRINT`, `WAKE_LOCK`, `RECEIVE_BOOT_COMPLETED`,
  `FOREGROUND_SERVICE` (library-added). No INTERNET, no storage, no SMS, no QUERY_ALL_PACKAGES.
  The `<queries>` list of nine named packages avoids the "package visibility" declaration (**verify in Play Console**).
- **Foreground service types:** Android 14+ Play form asks about foreground service use. The app declares no
  foreground service of its own; `FOREGROUND_SERVICE` comes from a library (**verify in Play Console**).
- **Government/news/health/other policy forms:** not applicable.
- **Export compliance / encryption:** the app uses standard encryption only (SQLCipher, AES-GCM); typically
  exempt, but US export rules are the developer's call (**verify**).

## 9. Gap list (what the app and repo must still do)

Confidence = how sure the finding is about the current repo state, not about the policy interpretation.

| # | Gap | Where | Confidence |
|---|---|---|---|
| 1 | **No dedicated prominent-disclosure screen with consent before opening notification-access settings.** Onboarding page 3 shows only a short body ("Fintrack bisa membaca notifikasi transaksi dari aplikasi bank dan e-wallet, lalu menunggu konfirmasimu...") and buttons "Aktifkan" / "Nanti saja". It does not say what data is read (title/text), that other apps are ignored, that nothing leaves the device, or that raw text is kept 30 days; the button is "Aktifkan", not an explicit consent. Two other entry points open the system settings directly with no disclosure: `feature/settings/.../MonitoredAppsRoute.kt` (`onOpenAccessSettings`) and `feature/inbox/.../InboxRoute.kt` (`Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS`). Strings: `feature/onboarding/src/main/res/values/strings.xml` lines 20-24. Fix: one shared disclosure dialog (text in section 7), shown before all three entry points, with "Setuju" and "Tidak sekarang". | `feature/onboarding/.../NotificationPage.kt`, `NotificationAccess.kt`, `OnboardingRoute.kt` | High that it is missing; medium-high that current wording would be judged insufficient |
| 2 | **No link to the privacy policy inside the app.** The About screen shows `about_privacy_statement` text only; no URL or link action exists (no `ACTION_VIEW` anywhere in `feature`, `app`, `core`). Add a "Kebijakan Privasi" row opening the URL via browser intent (opening a browser needs no INTERNET permission in this app). | `feature/settings/.../AboutScreen.kt`, `feature/settings/src/main/res/values/strings.xml` | High |
| 3 | **Privacy policy not yet published and `ardialbrian@gmail.com` is empty.** Play requires a reachable URL and a contact. File must be pushed to the public repo main branch. | `docs/play-store/privacy-policy.md` | High |
| 4 | **Target SDK:** merged release manifest shows `targetSdkVersion=37`, `minSdkVersion=26`. Google requires API 36+ for new apps and updates (deadline 31 Aug 2026, per fetched page). Compliant. No explicit `targetSdk` was found in the Gradle sources I grepped, so it is set indirectly (probably the compileSdk default); confirm it stays 36+ in the signed AAB. | `build-logic/convention/.../FintrackConfig.kt`, `app/build/intermediates/merged_manifest/benchmarkRelease/...` | Medium-high (read from a benchmark variant manifest, not the release AAB) |
| 5 | **Docs still say "not Play Store".** `docs/00-vision.md` lists distribution as signed APK only. Update (with an ADR) if Play distribution is decided. | `docs/00-vision.md`, `docs/16-decisions.md` | High |
| 6 | **`docs/10-security-privacy.md` permission table is incomplete** versus the merged manifest (`USE_FINGERPRINT`, `WAKE_LOCK`, `FOREGROUND_SERVICE` are not listed). Keep the Data safety answers and docs consistent. | `docs/10-security-privacy.md` | High |
| 7 | **Release must be an AAB signed for Play** (Play App Signing), and built from the release variant, not benchmark. Packaging and signing plan is not in the docs I read. | `app/build.gradle.kts`, `docs/12-git-workflow.md` | Low (not investigated in depth) |
| 8 | **Privacy-policy claims that rest on design docs, not code I verified:** 30-day raw-text purge (FR-092), discarding non-transaction text, SQLCipher+Keystore, PIN hashing, AES-256 backup. If any is not yet implemented when you publish, edit the policy first. Check `docs/20-phase.md` for completed tasks. | `docs/07-...`, `docs/10-...`, `docs/11-...` | Medium |
| 9 | **New personal developer accounts** may need closed testing with a minimum number of testers for a minimum period before production access (rule introduced for accounts created after Nov 2023). | Play Console | Low (not fetched; **verify in Play Console**) |
| 10 | **Store listing wording:** avoid implying affiliation with banks/e-wallets; describe the notification feature honestly; screenshots must not show real personal data (public repo rule ADR-013 applies to marketing assets too). | listing | Medium |

## 10. Open questions for the owner

1. Contact email to put in `ardialbrian@gmail.com` and in the Play listing (not guessed here).
2. Will the app be distributed on Play at all, or stay APK-only (`docs/00-vision.md`)? If Play, add an ADR.
3. Preferred target age (13+ or 18+).
4. Developer name/address: Play shows a public address for some account types (**verify in Play Console**).
