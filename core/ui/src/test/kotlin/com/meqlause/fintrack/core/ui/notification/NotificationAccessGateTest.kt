package com.meqlause.fintrack.core.ui.notification

import android.provider.Settings
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h720dp")
class NotificationAccessGateTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun startedIntentAction(): String? =
        shadowOf(RuntimeEnvironment.getApplication()).nextStartedActivity?.action

    private fun showGate() {
        composeRule.setContent {
            NotificationAccessGate { requestAccess ->
                TextButton(onClick = requestAccess) { Text(text = "Aktifkan") }
            }
        }
    }

    // DOC-10: the disclosure appears first and nothing opens until the user answers
    @Test
    fun `given access requested when clicked then the disclosure shows and settings stay closed (DOC-10)`() {
        showGate()
        composeRule.onNodeWithText("Aktifkan").performClick()
        composeRule.onNodeWithText("Izinkan Fintrack membaca notifikasi?").assertIsDisplayed()
        composeRule.onNodeWithText("Setuju").assertIsDisplayed()
        composeRule.onNodeWithText("Tidak sekarang").assertIsDisplayed()
        assertThat(startedIntentAction()).isNull()
    }

    // DOC-10: only "Setuju" opens the system notification-listener settings
    @Test
    fun `given the disclosure when agreed then the listener settings open (DOC-10)`() {
        showGate()
        composeRule.onNodeWithText("Aktifkan").performClick()
        composeRule.onNodeWithText("Setuju").performClick()
        assertThat(startedIntentAction()).isEqualTo(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        composeRule.onNodeWithText("Setuju").assertDoesNotExist()
    }

    // DOC-10: "Tidak sekarang" closes the disclosure and opens nothing
    @Test
    fun `given the disclosure when declined then nothing opens and the dialog closes (DOC-10)`() {
        showGate()
        composeRule.onNodeWithText("Aktifkan").performClick()
        composeRule.onNodeWithText("Tidak sekarang").performClick()
        assertThat(startedIntentAction()).isNull()
        composeRule.onNodeWithText("Tidak sekarang").assertDoesNotExist()
    }
}
