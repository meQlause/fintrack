package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

private val IndicatorWidth: Dp = 28.dp
private val IndicatorHeight: Dp = 2.dp

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
                .padding(
                    top = FintrackTheme.spacing.small,
                ).heightIn(min = FintrackTheme.sizes.touchTarget),
    ) {
        val tabWidth = maxWidth / types.size
        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * types.indexOf(selected).toFloat() + (tabWidth - IndicatorWidth) / 2,
            animationSpec = tween(durationMillis = motion.durationMillis, easing = motion.easing),
            label = "history-type-indicator",
        )
        Row(modifier = Modifier.fillMaxSize()) {
            types.forEach { type ->
                TypeTab(
                    type = type,
                    selected = type == selected,
                    onClick = { onPick(type) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(FintrackTheme.sizes.hairline)
                    .background(FintrackTheme.colors.surfaces.outline),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = indicatorOffset)
                    .width(IndicatorWidth)
                    .height(IndicatorHeight)
                    .clip(FintrackTheme.shapes.full)
                    .background(FintrackTheme.colors.texts.onSurface),
        )
    }
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
            style = FintrackTheme.typography.labelLarge,
            color = if (selected) colors.texts.onSurface else colors.texts.onSurfaceVariant,
        )
    }
}
