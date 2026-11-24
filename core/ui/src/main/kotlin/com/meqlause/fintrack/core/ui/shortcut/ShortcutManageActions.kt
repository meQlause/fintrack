package com.meqlause.fintrack.core.ui.shortcut

import com.meqlause.fintrack.core.model.QuickEntryId

// FR-039: what the manage list can do; the new and edit actions are optional, a host without a form leaves them null
public data class ShortcutManageActions(
    public val onDelete: (QuickEntryId) -> Unit,
    public val onNew: (() -> Unit)? = null,
    public val onEdit: ((QuickEntryId) -> Unit)? = null,
)
