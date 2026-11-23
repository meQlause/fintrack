package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R
import java.time.YearMonth

private val PillWidth: Dp = 64.dp
private val PillHeight: Dp = 36.dp

// BR-010, FR-036, S-05: the twelve months of the picked year in one row; a pill slides under the selected month
@Composable
internal fun HistoryMonthStrip(
    month: YearMonth,
    onPick: (YearMonth) -> Unit,
) {
    val names = stringArrayResource(R.array.history_month_names).toList()
    val selectedIndex = month.monthValue - 1
    val motion = FintrackTheme.motion.medium
    val indicatorOffset by animateDpAsState(
        targetValue = PillWidth * selectedIndex.toFloat(),
        animationSpec = tween(durationMillis = motion.durationMillis, easing = motion.easing),
        label = "history-month-indicator",
    )
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    top = FintrackTheme.spacing.medium,
                ).horizontalScroll(rememberScrollState()),
    ) {
        Box(
            modifier =
                Modifier
                    .offset(x = indicatorOffset)
                    .size(width = PillWidth, height = PillHeight)
                    .clip(FintrackTheme.shapes.full)
                    .background(FintrackTheme.colors.brands.primaryContainer),
        )
        Row {
            names.forEachIndexed { index, label ->
                MonthPill(
                    label = label,
                    selected = index == selectedIndex,
                    onClick = { onPick(YearMonth.of(month.year, index + 1)) },
                )
            }
        }
    }
}

@Composable
private fun MonthPill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = FintrackTheme.colors
    Box(
        modifier =
            Modifier
                .width(PillWidth)
                .height(PillHeight)
                .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = FintrackTheme.typography.labelLarge,
            color = if (selected) colors.brands.onPrimaryContainer else colors.texts.onSurfaceVariant,
        )
    }
}
