package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R
import java.time.YearMonth

// BR-010, FR-036, S-05: the picked year, then its twelve months in one row with a pill under the selected month
@Composable
internal fun HistoryMonthStrip(
    month: YearMonth,
    activeMonths: Set<YearMonth>,
    onPick: (YearMonth) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium)) {
        MonthPills(month = month, activeMonths = activeMonths, onPick = onPick)
    }
}

// BR-010: the row opens scrolled to the selected month, so the current month is on screen
@Composable
private fun MonthPills(
    month: YearMonth,
    activeMonths: Set<YearMonth>,
    onPick: (YearMonth) -> Unit,
) {
    val names = stringArrayResource(R.array.history_month_names).toList()
    val selectedIndex = month.monthValue - 1
    val motion = FintrackTheme.motion.medium
    val scrollState = rememberScrollState()
    val pillPx =
        with(LocalDensity.current) { (FintrackTheme.sizes.monthPillMock + FintrackTheme.spacing.xSmall).roundToPx() }
    val indicatorOffset by animateDpAsState(
        targetValue = (FintrackTheme.sizes.monthPillMock + FintrackTheme.spacing.xSmall) * selectedIndex.toFloat(),
        animationSpec = tween(durationMillis = motion.durationMillis, easing = motion.easing),
        label = "history-month-indicator",
    )
    LaunchedEffect(selectedIndex) { scrollState.animateScrollTo(selectedIndex * pillPx) }
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = FintrackTheme.spacing.gutter),
    ) {
        Box(
            modifier =
                Modifier
                    .offset(x = indicatorOffset)
                    .size(width = FintrackTheme.sizes.monthPillMock, height = FintrackTheme.sizes.monthPillHeight)
                    .clip(FintrackTheme.shapes.full)
                    .background(FintrackTheme.colors.texts.onSurface),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
            names.forEachIndexed { index, label ->
                MonthPill(
                    label = label,
                    selected = index == selectedIndex,
                    hasDot = YearMonth.of(month.year, index + 1) in activeMonths,
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
    hasDot: Boolean,
    onClick: () -> Unit,
) {
    val colors = FintrackTheme.colors
    Box(
        modifier =
            Modifier
                .width(FintrackTheme.sizes.monthPillMock)
                .height(FintrackTheme.sizes.monthPillHeight)
                .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val tone = if (selected) colors.surfaces.background else colors.texts.onSurfaceVariant
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = label, style = FintrackTheme.typography.tabLabel, color = tone)
            if (hasDot) {
                Box(
                    modifier =
                        Modifier
                            .padding(start = FintrackTheme.spacing.xSmall)
                            .size(FintrackScreenSizes.monthDot)
                            .background(if (selected) tone else colors.texts.faint, CircleShape),
                )
            }
        }
    }
}
