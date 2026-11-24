package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.LargeText

private const val LARGE_TAB_COUNT = 2.5f

// FR-037, S-05: the Semua, Pengeluaran, Pemasukan and Transfer tabs; the underline slides to the selected one
@Composable
internal fun HistoryTypeTabs(
    selected: HistoryTypeFilter,
    onPick: (HistoryTypeFilter) -> Unit,
) {
    val types = HistoryTypeFilter.entries
    val motion = FintrackTheme.motion.medium
    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = FintrackTheme.spacing.small / 2 + FintrackTheme.spacing.xxSmall)
                .height(FintrackTheme.sizes.typeTabHeight),
    ) {
        val tabWidth = if (LargeText.isOn) maxWidth / LARGE_TAB_COUNT else maxWidth / types.size
        val trackWidth = tabWidth * types.size.toFloat()
        val indicatorOffset by animateDpAsState(
            targetValue =
                tabWidth * types.indexOf(selected).toFloat() + (tabWidth - FintrackTheme.sizes.tabIndicatorWidth) / 2,
            animationSpec = tween(durationMillis = motion.durationMillis, easing = motion.easing),
            label = "history-type-indicator",
        )
        Box(modifier = if (LargeText.isOn) Modifier.horizontalScroll(rememberScrollState()) else Modifier) {
            TabRow(selected = selected, onPick = onPick, modifier = Modifier.width(trackWidth))
            TabUnderline(offset = indicatorOffset, modifier = Modifier.width(trackWidth))
        }
    }
}

// S-05: the tab row takes the height of its labels, so the tabs never claim the list's space
@Composable
private fun TabRow(
    selected: HistoryTypeFilter,
    onPick: (HistoryTypeFilter) -> Unit,
    modifier: Modifier,
) {
    Row(modifier = modifier.height(FintrackTheme.sizes.typeTabHeight)) {
        HistoryTypeFilter.entries.forEach { type ->
            TypeTab(
                type = type,
                selected = type == selected,
                onClick = { onPick(type) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// S-05: the hairline under the tabs and the short indicator on the selected one
@Composable
private fun BoxScope.TabUnderline(
    offset: Dp,
    modifier: Modifier,
) {
    Box(
        modifier =
            modifier
                .align(Alignment.BottomStart)
                .height(FintrackTheme.sizes.hairline)
                .background(FintrackTheme.colors.surfaces.outline),
    )
    Box(
        modifier =
            Modifier
                .align(Alignment.BottomStart)
                .offset { IntOffset(x = offset.roundToPx(), y = 0) }
                .width(FintrackTheme.sizes.tabIndicatorWidth)
                .height(FintrackTheme.sizes.tabIndicatorHeight)
                .clip(FintrackTheme.shapes.full)
                .background(FintrackTheme.colors.texts.onSurface),
    )
}

@Composable
private fun TypeTab(
    type: HistoryTypeFilter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(type.labelRes),
            style = FintrackTheme.typography.tabWide,
            color = if (selected) colors.texts.onSurface else colors.texts.onSurfaceVariant,
        )
    }
}
