package com.meqlause.fintrack.feature.category

// FR-022, BR-008: the answers to the offer to archive a category that is still in use; defaults do nothing
internal data class ArchiveOfferCallbacks(
    val onConfirm: () -> Unit = {},
    val onDismiss: () -> Unit = {},
)
