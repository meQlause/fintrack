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

/** FR-080, FR-090: notification access is granted only for an enabled listener of this package. */
@RunWith(RobolectricTestRunner::class)
class NotificationAccessCheckerTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val checker = NotificationAccessChecker(context)

    /** FR-080: with no listener enabled, notification access is not granted. */
    @Test
    fun `given no listener enabled when checked then access is not granted (FR-080)`() {
        assertThat(checker.isGranted()).isFalse()
    }

    /** FR-080: a listener of this package enabled grants notification access. */
    @Test
    fun `given a listener of this package enabled when checked then access is granted (FR-080)`() {
        enableListeners("${context.packageName}/${context.packageName}.Listener")

        assertThat(checker.isGranted()).isTrue()
    }

    /** FR-090: a listener of another package does not grant access to this app. */
    @Test
    fun `given only another package listener enabled when checked then access is not granted (FR-090)`() {
        enableListeners("com.other/com.other.Listener")

        assertThat(checker.isGranted()).isFalse()
    }

    private fun enableListeners(value: String) {
        Settings.Secure.putString(context.contentResolver, "enabled_notification_listeners", value)
    }
}
