package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.component.ScreenPrimaryButton
import com.meqlause.fintrack.core.designsystem.theme.FintrackCalendarSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

private const val HALF_TURN = 180f

// BR-010, FR-036, S-05: the period picker, a year row, the month tiles, the day grid and "Semua tanggal"
@Composable
internal fun HistoryMonthPicker(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val monthName = stringArrayResource(R.array.history_month_names)[state.month.monthValue - 1]
    PickerDrawer(
        title = stringResource(R.string.history_month_picker_title),
        onClose = { onEvent(HistoryEvent.SheetDismissed) },
    ) {
        YearRow(
            year = state.pickerYear,
            canAdvance = state.today?.let { state.pickerYear < it.year } ?: true,
            onShift = { onEvent(HistoryEvent.YearShifted(it)) },
        )
        HistoryMonthTiles(state = state, onEvent = onEvent)
        HistoryDayCalendar(state = state, onEvent = onEvent)
        ScreenPrimaryButton(
            text = stringResource(R.string.history_all_days, monthName, state.month.year),
            onClick = { onEvent(HistoryEvent.AllDaysPicked) },
            modifier = Modifier.padding(top = FintrackTheme.spacing.large),
        )
    }
}

// S-05: the chosen year between two chevrons; the next one is disabled once the year is the current one
@Composable
private fun YearRow(
    year: Int,
    canAdvance: Boolean,
    onShift: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        YearChevron(description = R.string.history_year_previous, turn = HALF_TURN, enabled = true) { onShift(-1) }
        Text(
            text = year.toString(),
            style = FintrackScreenText.style(size = 16f, weight = 700),
            color = FintrackTheme.colors.texts.onSurface,
        )
        YearChevron(description = R.string.history_year_next, turn = 0f, enabled = canAdvance) { onShift(1) }
    }
}

@Composable
private fun YearChevron(
    description: Int,
    turn: Float,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(FintrackCalendarSizes.chevronTarget)
                .clip(FintrackTheme.shapes.full)
                .alpha(if (enabled) 1f else FintrackCalendarSizes.disabledAlpha)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(DesignR.drawable.ic_chevron_right),
            contentDescription = stringResource(description),
            tint = FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.size(FintrackTheme.sizes.iconSmall).rotate(turn),
        )
    }
}
