package com.meqlause.fintrack.core.designsystem.component

// DOC-08: the label and the tap of the white pill under an illustrated empty state
data class EmptyAction(
    val label: String,
    val onClick: () -> Unit,
)
