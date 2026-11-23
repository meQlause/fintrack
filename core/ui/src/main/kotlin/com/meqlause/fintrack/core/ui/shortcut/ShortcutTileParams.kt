package com.meqlause.fintrack.core.ui.shortcut

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntryId

// FR-039, S-03, S-04: what one shortcut tile shows; built by the feature that owns the shortcut list
public data class ShortcutTileParams(
    public val quickEntryId: QuickEntryId,
    public val title: String,
    public val amount: Money,
    public val walletName: String,
)
