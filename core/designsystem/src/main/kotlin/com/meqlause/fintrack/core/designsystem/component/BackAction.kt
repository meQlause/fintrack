package com.meqlause.fintrack.core.designsystem.component

// DOC-08: the accessibility label of the back button and what pressing it does
data class BackAction(
    val label: String,
    val onBack: () -> Unit,
)
