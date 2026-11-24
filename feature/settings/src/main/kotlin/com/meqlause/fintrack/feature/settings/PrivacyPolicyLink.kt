package com.meqlause.fintrack.feature.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

// DOC-10: where the published privacy policy lives in the public repository
private const val POLICY_HOST = "github.com"
private const val POLICY_PATH = "/meQlause/fintrack/blob/main/docs/play-store/privacy-policy.md"

// DOC-10: opens the privacy policy in the browser; without any browser a toast says so and nothing else happens
internal fun openPrivacyPolicy(context: Context) {
    // DOC-10: a VIEW intent for the browser, so this app needs no INTERNET permission (NFR-001)
    val policy =
        Uri
            .Builder()
            .scheme("https")
            .authority(POLICY_HOST)
            .path(POLICY_PATH)
            .build()
    val intent = Intent(Intent.ACTION_VIEW, policy).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, R.string.about_no_browser, Toast.LENGTH_SHORT).show()
    }
}
