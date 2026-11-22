package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.toSpec

// DOC-08 principle 6: on/off setting; the knob slides with the quick motion token
@Composable
fun FintrackSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sizes = FintrackTheme.sizes
    val colors = FintrackTheme.colors
    val progress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = FintrackTheme.motion.quick.toSpec(),
    )
    val travel = sizes.switchWidth - sizes.knobSize - sizes.knobInset * 2
    Box(
        modifier =
            modifier
                .width(sizes.switchWidth)
                .height(sizes.switchHeight)
                .clip(FintrackTheme.shapes.full)
                .background(if (checked) colors.brands.primary else colors.surfaces.outline)
                .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier =
                Modifier
                    .offset { IntOffset(x = (sizes.knobInset + travel * progress).roundToPx(), y = 0) }
                    .size(sizes.knobSize)
                    .clip(FintrackTheme.shapes.full)
                    .background(colors.brands.onPrimary),
        )
    }
}

@Composable
internal fun FintrackSwitchSample() {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FintrackSwitch(checked = true, onCheckedChange = {})
        FintrackSwitch(checked = false, onCheckedChange = {})
    }
}

@Preview(name = "FintrackSwitch light", showBackground = true)
@Composable
internal fun FintrackSwitchLightPreview() {
    ComponentCanvas(darkTheme = false) { FintrackSwitchSample() }
}

@Preview(name = "FintrackSwitch dark", showBackground = true)
@Composable
internal fun FintrackSwitchDarkPreview() {
    ComponentCanvas(darkTheme = true) { FintrackSwitchSample() }
}
