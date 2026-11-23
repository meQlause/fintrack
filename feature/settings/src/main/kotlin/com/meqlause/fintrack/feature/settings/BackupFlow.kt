package com.meqlause.fintrack.feature.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.ui.picker.OptionChoices
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
import com.meqlause.fintrack.core.ui.picker.PickerActions
import com.meqlause.fintrack.core.ui.picker.PickerOption
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val DAYS_SHORT = 3
private const val DAYS_WEEK = 7
private const val DAYS_TWO_WEEKS = 14
private const val DAYS_MONTH = 30

private val REMINDER_CHOICES = listOf(0, DAYS_SHORT, DAYS_WEEK, DAYS_TWO_WEEKS, DAYS_MONTH)

// FR-110..FR-113: the backup dialogs of S-12, shown one at a time for the step the flow is in
@Composable
internal fun BackupFlow(
    state: BackupUiState,
    callbacks: BackupFlowCallbacks,
    onDismissStep: () -> Unit,
    onDismissOutcome: () -> Unit,
) {
    when (val step = state.step) {
        BackupStep.ExportPassword -> {
            BackupPasswordDialog(creating = true, onConfirm = callbacks.onPasswordForExport, onDismiss = onDismissStep)
        }

        is BackupStep.ImportPassword -> {
            BackupPasswordDialog(creating = false, onConfirm = callbacks.onPasswordForImport, onDismiss = onDismissStep)
        }

        is BackupStep.CsvRange -> {
            BackupCsvDialog(range = step.range, onConfirm = callbacks.onCsvRangePicked, onDismiss = onDismissStep)
        }

        else -> {
            BackupSheetStep(state = state, callbacks = callbacks, onDismiss = onDismissStep)
        }
    }
    state.outcome?.let { outcome -> OutcomeDialog(outcome = outcome, onDismiss = onDismissOutcome) }
}

// FR-112, FR-110: the restore summary and the reminder choice, the steps that are not a plain dialog
@Composable
private fun BackupSheetStep(
    state: BackupUiState,
    callbacks: BackupFlowCallbacks,
    onDismiss: () -> Unit,
) {
    when (val step = state.step) {
        is BackupStep.ConfirmRestore -> {
            RestoreSummaryDialog(
                snapshot = step.snapshot,
                onConfirm = callbacks.onConfirmRestore,
                onDismiss = onDismiss,
            )
        }

        BackupStep.ReminderDays -> {
            ReminderDrawer(
                selectedDays = state.reminderDays,
                onPick = callbacks.onReminderDaysPicked,
                onClose = onDismiss,
            )
        }

        else -> {
            Unit
        }
    }
}

// FR-110..FR-113: the answers the backup dialogs give, each one passes on to the ViewModel
internal data class BackupFlowCallbacks(
    val onPasswordForExport: (String) -> Unit,
    val onPasswordForImport: (String) -> Unit,
    val onConfirmRestore: () -> Unit,
    val onCsvRangePicked: (ClosedRange<LocalDate>) -> Unit,
    val onReminderDaysPicked: (Int) -> Unit,
)

// FR-112: the drawer that picks how many days pass between reminders
@Composable
private fun ReminderDrawer(
    selectedDays: Int,
    onPick: (Int) -> Unit,
    onClose: () -> Unit,
) {
    OptionsDrawer(
        title = stringResource(R.string.settings_backup_reminder_sheet_title),
        choices = OptionChoices(options = reminderOptions(), selectedKey = selectedDays.toString()),
        actions = PickerActions(onPick = { option -> onPick(option.optionKey.toInt()) }, onClose = onClose),
    )
}

// FR-112: the reminder choices; 0 means reminders are off
@Composable
private fun reminderOptions(): List<PickerOption> =
    REMINDER_CHOICES.map { days ->
        val label =
            if (days == 0) {
                stringResource(R.string.settings_backup_reminder_off)
            } else {
                pluralStringResource(R.plurals.settings_backup_reminder_value, days, days)
            }
        PickerOption(optionKey = days.toString(), label = label)
    }

// FR-111: the summary of a validated backup; nothing has been replaced yet
@Composable
private fun RestoreSummaryDialog(
    snapshot: BackupSnapshot,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.backup_restore_title)) },
        text = {
            Text(
                text =
                    pluralStringResource(
                        R.plurals.backup_restore_summary,
                        snapshot.ledger.wallets.size,
                        snapshot.ledger.wallets.size,
                        snapshot.ledger.transactions.size,
                        formatDate(snapshot.createdAt),
                    ) + "\n" + stringResource(R.string.backup_restore_warning),
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(text = stringResource(R.string.backup_restore_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(R.string.backup_cancel)) }
        },
    )
}

// FR-110..FR-113: the result of an export or restore, or the human reason it failed
@Composable
private fun OutcomeDialog(
    outcome: BackupOutcome,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(text = stringResource(outcomeRes(outcome))) },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(R.string.backup_ok)) }
        },
    )
}

internal fun outcomeRes(outcome: BackupOutcome): Int =
    when (outcome) {
        BackupOutcome.Saved -> R.string.backup_saved
        BackupOutcome.Restored -> R.string.backup_restored
        BackupOutcome.CsvSaved -> R.string.backup_csv_saved
        is BackupOutcome.Failed -> failureRes(outcome.failure)
    }

// FR-110, FR-111: Indonesian message of each failure reason
internal fun failureRes(failure: BackupFailure): Int =
    when (failure) {
        BackupFailure.PASSWORD_TOO_SHORT -> R.string.backup_password_too_short
        BackupFailure.TOO_LARGE -> R.string.backup_error_too_large
        BackupFailure.NOT_BACKUP -> R.string.backup_error_not_backup
        BackupFailure.UNSUPPORTED_VERSION -> R.string.backup_error_unsupported
        BackupFailure.WRONG_PASSWORD -> R.string.backup_error_wrong_password
        BackupFailure.CORRUPT -> R.string.backup_error_corrupt
        BackupFailure.READ_FAILED -> R.string.backup_error_read
        BackupFailure.WRITE_FAILED -> R.string.backup_error_write
    }

// FR-111: the creation date of a backup as "20 Nov 2026"
private fun formatDate(createdAt: Instant): String =
    DateTimeFormatter
        .ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))
        .withZone(ZoneId.systemDefault())
        .format(createdAt)
