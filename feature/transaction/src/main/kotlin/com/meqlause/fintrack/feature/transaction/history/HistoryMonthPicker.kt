package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R
import java.time.YearMonth

private const val MONTHS_PER_ROW = 4

// BR-010, FR-036, S-05: the month and year picker; the day of the month is picked from the Tanggal chip
@Composable
internal fun HistoryMonthPicker(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val names = stringArrayResource(R.array.history_month_names).toList()
    PickerDrawer(
        title = stringResource(R.string.history_month_picker_title),
        onClose = { onEvent(HistoryEvent.SheetDismissed) },
    ) {
        YearRow(year = state.pickerYear, onShift = { onEvent(HistoryEvent.YearShifted(it)) })
        names.chunked(MONTHS_PER_ROW).forEachIndexed { row, labels ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.small),
                horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
            ) {
                labels.forEachIndexed { column, label ->
                    val candidate = YearMonth.of(state.pickerYear, row * MONTHS_PER_ROW + column + 1)
                    FintrackChip(
                        label = label,
                        selected = candidate == state.month,
                        onClick = { onEvent(HistoryEvent.MonthPicked(candidate)) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun YearRow(
    year: Int,
    onShift: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TextButton(onClick = { onShift(-1) }) {
            Text(text = stringResource(R.string.history_year_previous))
        }
        Text(
            text = year.toString(),
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        TextButton(onClick = { onShift(1) }) {
            Text(text = stringResource(R.string.history_year_next))
        }
    }
}
