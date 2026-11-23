package com.meqlause.fintrack.feature.loan.detail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.feature.loan.R

// FR-155, BR-046: the confirmation before a loan is written off or deleted; nothing changes until it is confirmed
@Composable
internal fun LoanConfirmDialog(
    dialog: LoanDialog,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(titleRes(dialog))) },
        text = { Text(text = stringResource(bodyRes(dialog))) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(text = stringResource(confirmRes(dialog))) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(R.string.loan_dialog_cancel)) }
        },
    )
}

private fun titleRes(dialog: LoanDialog): Int =
    if (dialog == LoanDialog.WRITE_OFF) R.string.loan_writeoff_title else R.string.loan_delete_title

private fun bodyRes(dialog: LoanDialog): Int =
    if (dialog == LoanDialog.WRITE_OFF) R.string.loan_writeoff_body else R.string.loan_delete_body

private fun confirmRes(dialog: LoanDialog): Int =
    if (dialog == LoanDialog.WRITE_OFF) R.string.loan_writeoff_confirm else R.string.loan_delete_confirm
