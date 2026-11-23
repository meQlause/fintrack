package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-05, S-08: a pill of equal segments; the picked segment is raised on the surface colour
@Composable
internal fun <T> HistorySegmented(
    options: List<HistorySegmentOption<T>>,
    choice: SegmentChoice<T>,
    modifier: Modifier = Modifier,
    trackHeight: Dp = FintrackTheme.sizes.segmentTrackHeight,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(FintrackTheme.shapes.segmentTrack)
                .background(FintrackTheme.colors.surfaces.surfaceVariant)
                .padding(FintrackTheme.spacing.xxSmall + FintrackTheme.spacing.xxSmall / 2),
    ) {
        options.forEach { option ->
            HistorySegment(
                option = option,
                isSelected = option.value == choice.selected,
                onClick = { choice.onPick(option.value) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HistorySegment(
    option: HistorySegmentOption<*>,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .clip(FintrackTheme.shapes.segmentPill)
                .background(if (isSelected) colors.surfaces.surface else Color.Transparent)
                .selectable(selected = isSelected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val active = if (option.activeColor == Color.Unspecified) colors.texts.onSurface else option.activeColor
        val tone = if (isSelected) active else colors.texts.onSurfaceVariant
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall),
        ) {
            if (option.iconRes != null) {
                Icon(
                    painter = painterResource(option.iconRes),
                    contentDescription = null,
                    tint = tone,
                    modifier = Modifier.size(FintrackTheme.sizes.segmentGlyph),
                )
            }
            Text(text = option.label, style = FintrackTheme.typography.tabLabel, color = tone)
        }
    }
}
