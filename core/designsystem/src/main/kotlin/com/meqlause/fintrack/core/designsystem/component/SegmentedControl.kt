package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.toSpec

// DOC-08 principle 6: 2-4 mutually exclusive modes; the pill slides to the selected segment
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = FintrackTheme.sizes.segmentHeight)
                .clip(FintrackTheme.shapes.medium)
                .background(FintrackTheme.colors.surfaces.surfaceVariant)
                .padding(FintrackTheme.spacing.xxSmall),
    ) {
        val segmentWidth = maxWidth / options.size
        val pillOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = segmentMotion(),
        )
        SegmentPill(width = segmentWidth, offset = pillOffset)
        SegmentRow(options = options, selectedIndex = selectedIndex, onSelect = onSelect)
    }
}

// DOC-08: the row takes its segments' height, so the control never claims the space below it
@Composable
private fun SegmentRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .heightIn(min = FintrackTheme.sizes.segmentHeight),
    ) {
        options.forEachIndexed { index, label ->
            SegmentOption(
                label = label,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun segmentMotion(): AnimationSpec<Dp> = FintrackTheme.motion.medium.toSpec()

@Composable
private fun BoxScope.SegmentPill(
    width: Dp,
    offset: Dp,
) {
    Box(
        modifier =
            Modifier
                .matchParentSize()
                .width(width)
                .offset { IntOffset(x = offset.roundToPx(), y = 0) }
                .clip(FintrackTheme.shapes.small)
                .background(FintrackTheme.colors.surfaces.surface),
    )
}

@Composable
private fun SegmentOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val texts = FintrackTheme.colors.texts
    Box(
        modifier =
            modifier
                .fillMaxHeight()
                .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = FintrackTheme.typography.labelLarge,
            color = if (selected) texts.onSurface else texts.onSurfaceVariant,
        )
    }
}

@Composable
internal fun SegmentedControlSample() {
    SegmentedControl(
        options =
            listOf(
                stringResource(R.string.sample_type_out),
                stringResource(R.string.sample_type_in),
                stringResource(R.string.sample_type_transfer),
            ),
        selectedIndex = 1,
        onSelect = {},
    )
}

@Preview(name = "SegmentedControl light", showBackground = true)
@Composable
internal fun SegmentedControlLightPreview() {
    ComponentCanvas(darkTheme = false) { SegmentedControlSample() }
}

@Preview(name = "SegmentedControl dark", showBackground = true)
@Composable
internal fun SegmentedControlDarkPreview() {
    ComponentCanvas(darkTheme = true) { SegmentedControlSample() }
}
