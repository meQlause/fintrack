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
    val endDate = parseDate(endText)
    val valid = start != null && endDate != null && start <= endDate
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.backup_csv_title)) },
        text = {
            Column {
                DateField(startText, R.string.backup_csv_start_label, isError = start == null) { startText = it }
                DateField(endText, R.string.backup_csv_end_label, isError = endDate == null) { endText = it }
                Text(text = stringResource(R.string.backup_csv_hint, CSV_HINT_DATE))
                Text(text = stringResource(R.string.backup_csv_warning))
            }
        },
        confirmButton = {
            TextButton(
                enabled = valid,
                onClick = { if (start != null && endDate != null) onConfirm(start..endDate) },
            ) { Text(text = stringResource(R.string.backup_csv_action)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = stringResource(R.string.backup_cancel)) }
        },
    )
}

// FR-113: one typed date of the range, a single line that turns red when it is not a date
@Composable
private fun DateField(
    value: String,
    label: Int,
    isError: Boolean,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(text = stringResource(label)) },
        singleLine = true,
        isError = isError,
    )
}

// FR-113: a typed date as LocalDate, or null when it is not yyyy-MM-dd
private fun parseDate(text: String): LocalDate? = runCatching { LocalDate.parse(text) }.getOrNull()

// FR-113: the example date of the range hint, kept out of the string so lint does not take its hyphens for a dash
private const val CSV_HINT_DATE = "2026-11-01"
