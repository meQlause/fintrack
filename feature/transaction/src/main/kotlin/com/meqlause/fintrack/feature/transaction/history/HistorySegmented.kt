package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-05, S-08: a pill of equal segments; the picked segment is raised on the surface colour
@Composable
internal fun <T> HistorySegmented(
    options: List<HistorySegmentOption<T>>,
    selected: T,
    onPick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .heightIn(min = FintrackTheme.sizes.segmentHeight)
                .clip(FintrackTheme.shapes.full)
                .background(FintrackTheme.colors.surfaces.surfaceVariant)
                .padding(FintrackTheme.spacing.xxSmall),
    ) {
        options.forEach { option ->
            HistorySegment(
                label = option.label,
                isSelected = option.value == selected,
                onClick = { onPick(option.value) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HistorySegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .clip(FintrackTheme.shapes.full)
                .background(if (isSelected) colors.surfaces.surface else Color.Transparent)
                .selectable(selected = isSelected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = FintrackTheme.typography.labelLarge,
            color = if (isSelected) colors.texts.onSurface else colors.texts.onSurfaceVariant,
        )
    }
}
