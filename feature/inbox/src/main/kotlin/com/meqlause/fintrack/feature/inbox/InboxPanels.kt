package com.meqlause.fintrack.feature.inbox

// S-07: the screen flags and callbacks the body draws with, grouped so the body keeps a short parameter list
internal data class InboxPanels(
    val infoShown: Boolean,
    val resolvedOpen: Boolean,
    val onInfoDismiss: () -> Unit,
    val onToggleResolved: () -> Unit,
    val onPick: (InboxPicker) -> Unit,
)
