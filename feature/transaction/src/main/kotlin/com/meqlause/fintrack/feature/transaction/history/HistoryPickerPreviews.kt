package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import java.time.YearMonth

// S-05: light and dark previews of the period picker with its month tiles and its day grid
@Preview(name = "Period picker light", showBackground = true)
@Composable
internal fun HistoryPickerLightPreview() {
    FintrackTheme(darkTheme = false) { HistoryMonthPicker(state = samplePickerState(), onEvent = {}) }
}

@Preview(name = "Period picker dark", showBackground = true)
@Composable
internal fun HistoryPickerDarkPreview() {
    FintrackTheme(darkTheme = true) { HistoryMonthPicker(state = samplePickerState(), onEvent = {}) }
}

private const val SAMPLE_TODAY = 22

// S-05, DOC-08: November 2026 picked on the 22nd, Oct and Nov with data, December still ahead
private fun samplePickerState(): HistoryUiState {
    val november = YearMonth.parse("2026-11")
    val busyDays = 1..SAMPLE_TODAY
    return HistoryUiState(
        selection =
            HistorySelection(
                month = november,
                sheetState = HistorySheetState(sheet = HistorySheet.MONTH, pickerYear = 2026),
            ),
        status =
            HistoryStatus(
                isLoaded = true,
                activeMonths = setOf(YearMonth.parse("2026-10"), november),
                days =
                    HistoryDays(
                        activeDays = busyDays.map { november.atDay(it) }.toSet(),
                        today = november.atDay(SAMPLE_TODAY),
                    ),
            ),
    )
}
