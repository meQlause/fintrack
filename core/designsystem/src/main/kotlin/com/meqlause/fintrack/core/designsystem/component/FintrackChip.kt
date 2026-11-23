package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: pill filter chip; the selected chip takes the primary container colours
@Composable
fun FintrackChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val containerColor = if (selected) colors.brands.primaryContainer else colors.surfaces.surface
    val contentColor = if (selected) colors.brands.onPrimaryContainer else colors.texts.onSurface
    Box(
        modifier =
            modifier
                .minimumInteractiveComponentSize()
                .selectable(selected = selected, role = Role.Checkbox, onClick = onClick)
                .heightIn(min = FintrackTheme.sizes.chipHeight)
                .clip(FintrackTheme.shapes.full)
                .background(containerColor)
                .border(FintrackTheme.sizes.hairline, colors.surfaces.outline, FintrackTheme.shapes.full)
                .padding(horizontal = FintrackTheme.spacing.large),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label, style = FintrackTheme.typography.labelLarge, color = contentColor)
    }
}

@Composable
internal fun FintrackChipSample() {
    Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FintrackChip(label = stringResource(R.string.sample_chip_all), selected = true, onClick = {})
        FintrackChip(label = stringResource(R.string.sample_type_in), selected = false, onClick = {})
    }
}

@Preview(name = "FintrackChip light", showBackground = true)
@Composable
internal fun FintrackChipLightPreview() {
    ComponentCanvas(darkTheme = false) { FintrackChipSample() }
}

@Preview(name = "FintrackChip dark", showBackground = true)
@Composable
internal fun FintrackChipDarkPreview() {
    ComponentCanvas(darkTheme = true) { FintrackChipSample() }
}
