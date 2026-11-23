package com.meqlause.fintrack.core.ui.picker

// S-18: the options of a single-choice drawer and the key of the one already chosen
data class OptionChoices(
    val options: List<PickerOption>,
    val selectedKey: String?,
)
