package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: primary action button, full width, at least the 48 dp touch target, labelLarge text
@Composable
fun FintrackButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = FintrackTheme.colors
    val containerColor = if (enabled) colors.brands.primary else colors.surfaces.surfaceVariant
    val contentColor = if (enabled) colors.brands.onPrimary else colors.texts.faint
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = FintrackTheme.sizes.touchTarget)
                .clip(FintrackTheme.shapes.medium)
                .background(containerColor)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = FintrackTheme.typography.labelLarge, color = contentColor)
    }
}

@Composable
internal fun FintrackButtonSample() {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FintrackButton(text = stringResource(R.string.sample_save), onClick = {})
        FintrackButton(text = stringResource(R.string.sample_save), onClick = {}, enabled = false)
    }
}

@Preview(name = "FintrackButton light", showBackground = true)
@Composable
internal fun FintrackButtonLightPreview() {
    ComponentCanvas(darkTheme = false) { FintrackButtonSample() }
}

@Preview(name = "FintrackButton dark", showBackground = true)
@Composable
internal fun FintrackButtonDarkPreview() {
    ComponentCanvas(darkTheme = true) { FintrackButtonSample() }
}
