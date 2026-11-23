package com.meqlause.fintrack.feature.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.BackupSnapshot
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// FR-110, FR-111: the backup dialogs of S-12, shown one at a time for the step the flow is in
@Composable
internal fun BackupFlow(
    state: BackupUiState,
    onPasswordForExport: (String) -> Unit,
    onPasswordForImport: (String) -> Unit,
    onConfirmRestore: () -> Unit,
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

        null -> {
            Unit
        }
    }
    state.outcome?.let { outcome -> OutcomeDialog(outcome = outcome, onDismiss = onDismissOutcome) }
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
                        formatDate(snapshot),
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

// FR-110, FR-111: the result of an export or restore, or the human reason it failed
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
private fun formatDate(snapshot: BackupSnapshot): String =
    DateTimeFormatter
        .ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))
        .withZone(ZoneId.systemDefault())
        .format(snapshot.createdAt)
