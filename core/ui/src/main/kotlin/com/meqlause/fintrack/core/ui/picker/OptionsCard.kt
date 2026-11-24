package com.meqlause.fintrack.core.ui.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.component.ScreenCard

// DOC-08: the card of option rows with dividers, for a drawer that adds its own content around it
@Composable
fun OptionsCard(
    choices: OptionChoices,
    onPick: (PickerOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenCard(modifier = modifier) {
        choices.options.forEachIndexed { index, option ->
            OptionRow(
                option = option,
                isSelected = option.optionKey == choices.selectedKey,
                showDivider = index > 0,
                onClick = { onPick(option) },
            )
        }
    }
}
