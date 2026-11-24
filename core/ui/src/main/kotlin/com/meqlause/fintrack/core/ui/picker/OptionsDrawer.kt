package com.meqlause.fintrack.core.ui.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.UiPreviewCanvas

// S-18, DOC-08: single-choice options drawer, one card of option rows; picking closes it via the caller
@Composable
fun OptionsDrawer(
    title: String,
    choices: OptionChoices,
    actions: PickerActions<PickerOption>,
    modifier: Modifier = Modifier,
) {
    PickerDrawer(title = title, onClose = actions.onClose, modifier = modifier) {
        OptionsCard(choices = choices, onPick = actions.onPick)
    }
}

@Composable
internal fun OptionsDrawerSample() {
    OptionsDrawer(
        title = stringResource(R.string.picker_sample_title_due),
        choices = OptionChoices(options = sampleOptions(), selectedKey = "month"),
        actions = PickerActions(onPick = {}, onClose = {}),
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
