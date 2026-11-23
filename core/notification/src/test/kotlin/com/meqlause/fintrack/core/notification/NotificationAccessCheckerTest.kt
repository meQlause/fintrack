package com.meqlause.fintrack.core.notification

import android.content.Context
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// FR-080, FR-090: access is granted only while a listener of this package is enabled in the system settings
@RunWith(RobolectricTestRunner::class)
class NotificationAccessCheckerTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val checker = NotificationAccessChecker(context)

    @Test
    fun `given no listener enabled when checked then access is not granted (FR-080)`() {
        assertThat(checker.isGranted()).isFalse()
    }

    @Test
    fun `given a listener of this package enabled when checked then access is granted (FR-080)`() {
        enableListeners("${context.packageName}/${context.packageName}.Listener")

        assertThat(checker.isGranted()).isTrue()
    }

    @Test
    fun `given only another package listener enabled when checked then access is not granted (FR-090)`() {
        enableListeners("com.other/com.other.Listener")

        assertThat(checker.isGranted()).isFalse()
    }

    private fun enableListeners(value: String) {
        Settings.Secure.putString(context.contentResolver, "enabled_notification_listeners", value)
    }
}
