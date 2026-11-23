package com.meqlause.fintrack.feature.credit.instalment

// FR-144, FR-145, S-16: every action of the instalment detail, passed to the stateless screen as plain lambdas
internal data class InstalmentDetailCallbacks(
    val onBack: () -> Unit = {},
    val onPayOff: () -> Unit = {},
    val onRequestDelete: () -> Unit = {},
    val onDismissDelete: () -> Unit = {},
    val onConfirmDelete: () -> Unit = {},
)
