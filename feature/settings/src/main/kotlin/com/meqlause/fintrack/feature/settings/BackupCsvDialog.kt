package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import java.time.LocalDate

// FR-113: the date range dialog of "Ekspor CSV"; dates are typed as yyyy-MM-dd and checked before saving
@Composable
internal fun BackupCsvDialog(
    range: ClosedRange<LocalDate>,
    onConfirm: (ClosedRange<LocalDate>) -> Unit,
    onDismiss: () -> Unit,
) {
    var startText by remember { mutableStateOf(range.start.toString()) }
    var endText by remember { mutableStateOf(range.endInclusive.toString()) }
    val start = parseDate(startText)
    val end = parseDate(endText)
    val valid = start != null && end != null && start <= end
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.backup_csv_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = startText,
                    onValueChange = { startText = it },
                    label = { Text(text = stringResource(R.string.backup_csv_start_label)) },
                    singleLine = true,
                    isError = start == null,
                )
                OutlinedTextField(
                    value = endText,
                    onValueChange = { endText = it },
                    label = { Text(text = stringResource(R.string.backup_csv_end_label)) },
                    singleLine = true,
                    isError = end == null,
                )
                Text(text = stringResource(R.string.backup_csv_hint))
                Text(text = stringResource(R.string.backup_csv_warning))
            }
        },
        confirmButton = {
            TextButton(
                enabled = valid,
                onClick = { if (start != null && end != null) onConfirm(start..end) },
            ) { Text(text = stringResource(R.string.backup_csv_action)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(R.string.backup_cancel)) }
        },
    )
}

// FR-113: a typed date as LocalDate, or null when it is not yyyy-MM-dd
private fun parseDate(text: String): LocalDate? = runCatching { LocalDate.parse(text) }.getOrNull()
