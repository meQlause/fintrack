package com.meqlause.fintrack.feature.settings

import android.app.Application
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class PrivacyPolicyLinkTest {
    private val application = ApplicationProvider.getApplicationContext<Application>()

    // DOC-10: the privacy-policy row hands the published policy URL to the browser as a VIEW intent
    @Test
    fun `given a browser when the policy opens then a VIEW intent for the policy url starts (DOC-10)`() {
        openPrivacyPolicy(application)

        val started = shadowOf(application).nextStartedActivity
        assertThat(started.action).isEqualTo(Intent.ACTION_VIEW)
        assertThat(started.dataString).isEqualTo(
            "https://github.com/meQlause/fintrack/blob/main/docs/play-store/privacy-policy.md",
        )
    }

    // DOC-10: without a browser the app does not crash; a toast explains
    @Test
    fun `given no browser when the policy opens then a toast says so and nothing starts (DOC-10)`() {
        shadowOf(application).checkActivities(true)

        openPrivacyPolicy(application)

        assertThat(shadowOf(application).nextStartedActivity).isNull()
        assertThat(ShadowToast.getTextOfLatestToast()).isEqualTo("Tidak ada aplikasi browser")
    }
}
