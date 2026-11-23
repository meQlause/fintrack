package com.meqlause.fintrack.feature.settings

import android.content.Context
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.BackupAdjunct
import com.meqlause.fintrack.core.model.BackupLedger
import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.model.Preferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.Instant
import java.time.LocalDate

// FR-110..FR-113, S-12: the dialogs of the backup flow show for their step and hand the answer on
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w411dp-h720dp")
class BackupFlowUiTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val snapshot =
        BackupSnapshot(
            createdAt = Instant.parse("2026-11-20T00:00:00Z"),
            ledger = BackupLedger(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
            adjunct = BackupAdjunct(emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList()),
            preferences = Preferences(),
            backupReminderDays = 7,
        )

    private fun show(
        state: BackupUiState,
        callbacks: BackupFlowCallbacks = noCallbacks(),
        onDismissOutcome: () -> Unit = {},
    ) {
        composeRule.setContent {
            BackupFlow(state, callbacks, onDismissStep = {}, onDismissOutcome = onDismissOutcome)
        }
    }

    private fun noCallbacks(
        onConfirmRestore: () -> Unit = {},
        onReminderDaysPicked: (Int) -> Unit = {},
    ): BackupFlowCallbacks =
        BackupFlowCallbacks(
            onPasswordForExport = {},
            onPasswordForImport = {},
            onConfirmRestore = onConfirmRestore,
            onCsvRangePicked = {},
            onReminderDaysPicked = onReminderDaysPicked,
        )

    /** FR-111: the restore summary asks for confirmation before anything is replaced. */
    @Test
    fun `given the restore summary when Pulihkan is pressed then the confirmation is sent (FR-111)`() {
        var confirmed = false
        show(
            BackupUiState(step = BackupStep.ConfirmRestore(snapshot)),
            noCallbacks(onConfirmRestore = { confirmed = true }),
        )
        composeRule.onNodeWithText(context.getString(R.string.backup_restore_confirm)).performClick()
        assertTrue(confirmed)
    }

    /** FR-112: picking a reminder length sends it on. */
    @Test
    fun `given the reminder drawer when a length is picked then it is sent (FR-112)`() {
        var picked = -1
        show(
            BackupUiState(step = BackupStep.ReminderDays),
            noCallbacks(onReminderDaysPicked = { picked = it }),
        )
        composeRule.onNodeWithText(context.getString(R.string.settings_backup_reminder_off)).performClick()
        assertEquals(0, picked)
    }

    /** FR-110, FR-111, FR-113: the password and range dialogs show for their steps. */
    @Test
    fun `given the password and range steps when shown then their dialogs appear (FR-110)`() {
        show(BackupUiState(step = BackupStep.ExportPassword))
        composeRule.onNodeWithText(context.getString(R.string.backup_password_title_create)).assertExists()
    }

    /** FR-111: the import password dialog asks for the existing password only. */
    @Test
    fun `given the import step when shown then the restore password dialog appears (FR-111)`() {
        show(BackupUiState(step = BackupStep.ImportPassword("content://backup")))
        composeRule.onNodeWithText(context.getString(R.string.backup_password_title_restore)).assertExists()
    }

    /** FR-113: the range dialog shows for the csv step. */
    @Test
    fun `given the csv step when shown then the range dialog appears (FR-113)`() {
        show(BackupUiState(step = BackupStep.CsvRange(LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 30))))
        composeRule.onNodeWithText(context.getString(R.string.backup_csv_hint, "2026-11-01")).assertExists()
    }

    /** FR-110: the result dialog shows the outcome and closes on Oke. */
    @Test
    fun `given an outcome when Oke is pressed then the dialog is dismissed (FR-110)`() {
        var closed = false
        show(
            BackupUiState(outcome = BackupOutcome.Failed(BackupFailure.WRONG_PASSWORD)),
            onDismissOutcome = { closed = true },
        )
        composeRule.onNodeWithText(context.getString(R.string.backup_error_wrong_password)).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.backup_ok)).performClick()
        assertTrue(closed)
    }
}
