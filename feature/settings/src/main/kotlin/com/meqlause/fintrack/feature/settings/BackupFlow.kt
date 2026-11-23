package com.meqlause.fintrack.feature.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
import com.meqlause.fintrack.core.ui.picker.PickerOption
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val REMINDER_CHOICES = listOf(0, 3, 7, 14, 30)

// FR-110..FR-113: the backup dialogs of S-12, shown one at a time for the step the flow is in
@Composable
internal fun BackupFlow(
    state: BackupUiState,
    onPasswordForExport: (String) -> Unit,
    onPasswordForImport: (String) -> Unit,
    onConfirmRestore: () -> Unit,
    onCsvRangePicked: (ClosedRange<LocalDate>) -> Unit,
    onReminderDaysPicked: (Int) -> Unit,
    onDismissStep: () -> Unit,
    onDismissOutcome: () -> Unit,
) {
    when (val step = state.step) {
        BackupStep.ExportPassword -> {
            BackupPasswordDialog(creating = true, onConfirm = onPasswordForExport, onDismiss = onDismissStep)
        }

        is BackupStep.ImportPassword -> {
            BackupPasswordDialog(creating = false, onConfirm = onPasswordForImport, onDismiss = onDismissStep)
        }

        is BackupStep.ConfirmRestore -> {
            RestoreSummaryDialog(snapshot = step.snapshot, onConfirm = onConfirmRestore, onDismiss = onDismissStep)
        }

        is BackupStep.CsvRange -> {
            BackupCsvDialog(range = step.range, onConfirm = onCsvRangePicked, onDismiss = onDismissStep)
        }

        BackupStep.ReminderDays -> {
            OptionsDrawer(
                title = stringResource(R.string.settings_backup_reminder_sheet_title),
                options = reminderOptions(),
                selectedKey = state.reminderDays.toString(),
                onPick = { option -> onReminderDaysPicked(option.key.toInt()) },
                onClose = onDismissStep,
            )
        }

        null -> {
            Unit
        }
    }
    state.outcome?.let { outcome -> OutcomeDialog(outcome = outcome, onDismiss = onDismissOutcome) }
}

// FR-112: the reminder choices; 0 means reminders are off
@Composable
private fun reminderOptions(): List<PickerOption> =
    REMINDER_CHOICES.map { days ->
        val label =
            if (days == 0) {
                stringResource(R.string.settings_backup_reminder_off)
            } else {
                stringResource(R.string.settings_backup_reminder_value, days)
            }
        PickerOption(key = days.toString(), label = label)
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
                    stringResource(
                        R.string.backup_restore_summary,
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

private fun outcomeRes(outcome: BackupOutcome): Int =
    when (outcome) {
        BackupOutcome.Saved -> R.string.backup_saved
        BackupOutcome.Restored -> R.string.backup_restored
        BackupOutcome.CsvSaved -> R.string.backup_csv_saved
        is BackupOutcome.Failed -> failureRes(outcome.failure)
    }

// FR-110, FR-111: Indonesian message of each failure reason
private fun failureRes(failure: BackupFailure): Int =
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
