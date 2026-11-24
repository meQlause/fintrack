package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackCalendarSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R
import java.time.YearMonth

private const val MONTHS_PER_ROW = 4

// S-05: what one month tile shows
private data class TileMark(
    val isSelected: Boolean,
    val hasData: Boolean,
    val isFuture: Boolean,
)

// S-05: the twelve months of the picked year in 4 by 3 tiles; picking one shows its days below
@Composable
internal fun HistoryMonthTiles(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val names = stringArrayResource(R.array.history_month_names).toList()
    val latest = state.today?.let { YearMonth.from(it) }
    names.chunked(MONTHS_PER_ROW).forEachIndexed { row, labels ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            labels.forEachIndexed { column, label ->
                val month = YearMonth.of(state.pickerYear, row * MONTHS_PER_ROW + column + 1)
                val mark = TileMark(month == state.month, month in state.activeMonths, latest != null && month > latest)
                MonthTile(
                    label = label,
                    mark = mark,
                    onClick = { onEvent(HistoryEvent.MonthShown(month)) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun MonthTile(
    label: String,
    mark: TileMark,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val colors = FintrackTheme.colors
    val shape = FintrackTheme.shapes.medium
    val fill = if (mark.isSelected) colors.texts.onSurface else colors.surfaces.surface
    val edge = if (mark.isSelected) colors.brands.primary else colors.surfaces.outline
    Column(
        modifier =
            modifier
                .height(FintrackCalendarSizes.monthTile)
                .alpha(if (mark.isFuture) FintrackCalendarSizes.disabledAlpha else 1f)
                .clip(shape)
                .background(fill)
                .border(FintrackTheme.sizes.hairline, edge, shape)
                .clickable(enabled = !mark.isFuture, role = Role.Button, onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(FintrackCalendarSizes.dotGap, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = FintrackScreenText.style(size = 13f, weight = 600),
            color = if (mark.isSelected) colors.surfaces.background else colors.texts.onSurface,
        )
        if (mark.hasData) {
            DataDot()
        }
    }
}

// S-05: the small dot that marks a month or a day that has transactions
@Composable
internal fun DataDot() {
    Row(
        modifier =
            Modifier
                .size(FintrackScreenSizes.monthDot)
                .clip(FintrackTheme.shapes.full)
                .background(FintrackTheme.colors.texts.faint),
    ) {}
}
