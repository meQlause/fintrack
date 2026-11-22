package com.meqlause.fintrack.feature.onboarding

import android.app.Application
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows

@RunWith(RobolectricTestRunner::class)
class NotificationAccessTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    // FR-003 AC: access counts as granted only when a listener of this app is enabled in the system settings
    @Test
    fun `given no listener enabled when checked then access is not granted (FR-003)`() {
        assertThat(NotificationAccess.isGranted(context)).isFalse()
    }

    @Test
    fun `given a listener of this app enabled when checked then access is granted (FR-003)`() {
        val listener = "${context.packageName}/${context.packageName}.Listener"
        Settings.Secure.putString(context.contentResolver, "enabled_notification_listeners", listener)

        assertThat(NotificationAccess.isGranted(context)).isTrue()
    }

    @Test
    fun `given only another app listener enabled when checked then access is not granted (FR-003)`() {
        Settings.Secure.putString(context.contentResolver, "enabled_notification_listeners", "com.other/com.other.L")

        assertThat(NotificationAccess.isGranted(context)).isFalse()
    }

    // FR-003: "Aktifkan" opens the system page where the access is granted
    @Test
    fun `given enable tapped when settings opened then the notification settings activity is started (FR-003)`() {
        val application = ApplicationProvider.getApplicationContext<Application>()

        NotificationAccess.openSettings(application)

        val started = Shadows.shadowOf(application).nextStartedActivity
        assertThat(started.action).isEqualTo(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
    }
}
