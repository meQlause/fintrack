package com.meqlause.fintrack.core.ui.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.UiPreviewCanvas

// S-18: single-choice options drawer (loan due date, FR-158); picking closes it via the caller
@Composable
fun OptionsDrawer(
    title: String,
    options: List<PickerOption>,
    selectedKey: String?,
    onPick: (PickerOption) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PickerDrawer(title = title, onClose = onClose, modifier = modifier) {
        options.forEach { option ->
            OptionItem(
                option = option,
                isSelected = option.optionKey == selectedKey,
                onClick = { onPick(option) },
            )
        }
    }
}

@Composable
private fun OptionItem(
    option: PickerOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = FintrackTheme.colors
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = FintrackTheme.sizes.formRowHeight)
                .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
                .padding(vertical = FintrackTheme.spacing.xSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.label,
                style = FintrackTheme.typography.titleMedium,
                color = colors.texts.onSurface,
            )
            option.hint?.let { hint ->
                Text(
                    text = hint,
                    style = FintrackTheme.typography.bodyMedium,
                    color = colors.texts.onSurfaceVariant,
                )
            }
        }
        RadioButton(selected = isSelected, onClick = null)
    }
}

@Composable
internal fun OptionsDrawerSample() {
    OptionsDrawer(
        title = stringResource(R.string.picker_sample_title_due),
        options = sampleOptions(),
        selectedKey = "month",
        onPick = {},
        onClose = {},
    )
}

@Composable
private fun sampleOptions(): List<PickerOption> =
    listOf(
        PickerOption(optionKey = "none", label = stringResource(R.string.picker_sample_option_none)),
        PickerOption(optionKey = "week", label = stringResource(R.string.picker_sample_option_week)),
        PickerOption(optionKey = "month", label = stringResource(R.string.picker_sample_option_month)),
    )

@Preview(name = "OptionsDrawer light", showBackground = true)
@Composable
internal fun OptionsDrawerLightPreview() {
    UiPreviewCanvas(darkTheme = false) { OptionsDrawerSample() }
}

@Preview(name = "OptionsDrawer dark", showBackground = true)
@Composable
internal fun OptionsDrawerDarkPreview() {
    UiPreviewCanvas(darkTheme = true) { OptionsDrawerSample() }
}
