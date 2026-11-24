package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.meqlause.fintrack.core.designsystem.theme.FintrackCalendarSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R
import java.time.LocalDate

private const val DAYS_PER_WEEK = 7
private const val BLANK = 0

// S-05: what one day cell shows
private data class DayMark(
    val isToday: Boolean,
    val isPicked: Boolean,
    val hasData: Boolean,
    val isFuture: Boolean,
)

// S-05: the days of the picked month under a Sen to Min header; a tap filters the list to that day
@Composable
internal fun HistoryDayCalendar(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val cells =
        List(
            state.month
                .atDay(1)
                .dayOfWeek.value - 1,
        ) { BLANK } + (1..state.month.lengthOfMonth())
    Column(modifier = Modifier.padding(top = FintrackTheme.spacing.large)) {
        WeekdayHeader()
        cells.chunked(DAYS_PER_WEEK).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(DAYS_PER_WEEK) { slot ->
                    DayCell(dayOfMonth = week.getOrNull(slot) ?: BLANK, state = state, onEvent = onEvent)
                }
            }
        }
    }
}

@Composable
private fun WeekdayHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        stringArrayResource(R.array.history_weekday_names).forEach { name ->
            Text(
                text = name,
                style = FintrackScreenText.label(),
                color = FintrackTheme.colors.texts.faint,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f).padding(bottom = FintrackTheme.spacing.small),
            )
        }
    }
}

@Composable
private fun RowScope.DayCell(
    dayOfMonth: Int,
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val cell = Modifier.weight(1f).height(FintrackCalendarSizes.dayCell)
    if (dayOfMonth == BLANK) {
        Column(modifier = cell) {}
        return
    }
    val date = state.month.atDay(dayOfMonth)
    val mark =
        DayMark(
            isToday = date == state.today,
            isPicked = state.filter.dayOfMonth == dayOfMonth,
            hasData = date in state.activeDays,
            isFuture = state.isAfterToday(date),
        )
    DayContent(
        dayOfMonth = dayOfMonth,
        mark = mark,
        onClick = { onEvent(HistoryEvent.DayPicked(dayOfMonth)) },
        modifier = cell,
    )
}

@Composable
private fun DayContent(
    dayOfMonth: Int,
    mark: DayMark,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val colors = FintrackTheme.colors
    val shape = FintrackTheme.shapes.medium
    val ring = Modifier.border(FintrackCalendarSizes.ringWidth, colors.brands.primary, shape)
    Column(
        modifier =
            modifier
                .alpha(if (mark.isFuture) FintrackCalendarSizes.disabledAlpha else 1f)
                .clip(shape)
                .background(if (mark.isPicked) colors.brands.primaryContainer else colors.surfaces.background)
                .then(if (mark.isToday) ring else Modifier)
                .clickable(enabled = !mark.isFuture, role = Role.Button, onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(FintrackCalendarSizes.dotGap, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = dayOfMonth.toString(),
            style = FintrackScreenText.number(size = 14f, weight = 500),
            color = colors.texts.onSurface,
        )
        if (mark.hasData) {
            DataDot()
        }
    }
}

private fun HistoryUiState.isAfterToday(date: LocalDate): Boolean = today?.let { date > it } ?: false
