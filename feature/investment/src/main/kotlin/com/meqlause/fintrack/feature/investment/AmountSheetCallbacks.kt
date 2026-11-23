package com.meqlause.fintrack.feature.investment

// FR-170, FR-172: the keypad, save and close actions of an investment amount sheet
internal data class AmountSheetCallbacks(
    val onDigit: (String) -> Unit,
    val onBackspace: () -> Unit,
    val onSave: () -> Unit,
    val onDismiss: () -> Unit,
)
