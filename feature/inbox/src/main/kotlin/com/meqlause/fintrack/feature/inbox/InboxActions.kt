package com.meqlause.fintrack.feature.inbox

// S-07: every user action of the screen, each one a single ViewModel call
internal data class InboxActions(
    val onBack: () -> Unit = {},
    val onDismissAll: () -> Unit = {},
    val onUndo: () -> Unit = {},
    val onUndoShown: () -> Unit = {},
    val onEnableAccess: () -> Unit = {},
    val items: InboxItemActions = InboxItemActions(),
)

// S-07: the actions on one inbox item, a single card or a pair
internal data class InboxItemActions(
    val onConfirm: (Long) -> Unit = {},
    val onDismiss: (Long) -> Unit = {},
    val onConfirmPair: (Long) -> Unit = {},
    val onSplitPair: (Long) -> Unit = {},
    val onChooseWallet: (Long, Long) -> Unit = { _, _ -> },
    val onChooseCategory: (Long, Long) -> Unit = { _, _ -> },
)
