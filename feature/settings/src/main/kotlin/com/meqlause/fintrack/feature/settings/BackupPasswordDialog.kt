package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

private const val MIN_PASSWORD_LENGTH = 8

// FR-110, FR-111: asks for the backup password; creating a backup asks twice and both entries must match
@Composable
internal fun BackupPasswordDialog(
    creating: Boolean,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val firstState = rememberTextFieldState()
    val repeatState = rememberTextFieldState()
    val password = firstState.text.toString()
    val repeated = repeatState.text.toString()
    val error = passwordError(creating, password, repeated)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(titleRes(creating))) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedSecureTextField(
                    state = firstState,
                    label = { Text(text = stringResource(R.string.backup_password_label)) },
                )
                if (creating) {
                    OutlinedSecureTextField(
                        state = repeatState,
                        label = { Text(text = stringResource(R.string.backup_password_repeat_label)) },
                        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
                    )
                    Text(
                        text = stringResource(R.string.backup_password_warning),
                        style = FintrackTheme.typography.bodyLarge,
                        color = FintrackTheme.colors.texts.onSurfaceVariant,
                        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
                    )
                }
                error?.let { messageRes ->
                    Text(
                        text = stringResource(messageRes),
                        style = FintrackTheme.typography.bodyLarge,
                        color = FintrackTheme.colors.texts.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(password) },
                enabled = error == null,
            ) { Text(text = stringResource(R.string.backup_password_action)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(R.string.backup_cancel)) }
        },
    )
}

private fun titleRes(creating: Boolean): Int =
    if (creating) R.string.backup_password_title_create else R.string.backup_password_title_restore

// FR-110: the reason a new backup password cannot be used yet, or null when it can; restores accept any entry
private fun passwordError(
    creating: Boolean,
    password: String,
    repeated: String,
): Int? =
    when {
        !creating -> null
        password.length < MIN_PASSWORD_LENGTH -> R.string.backup_password_too_short
        repeated != password -> R.string.backup_password_mismatch
        else -> null
    }
