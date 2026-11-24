package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.QuickEntryId

/** DOC-09, S-03: entries into the money screens, history, instalments, loans and the shortcut form (FR-039). */
public data class HomeEntryLinks(
    val onOpenHistory: () -> Unit = {},
    val onOpenInstalments: () -> Unit = {},
    val onOpenLoans: () -> Unit = {},
    val onNewShortcut: () -> Unit = {},
    val onEditShortcut: (QuickEntryId) -> Unit = {},
    val onApplyShortcut: (QuickEntryId) -> Unit = {},
)
