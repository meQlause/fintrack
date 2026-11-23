package com.meqlause.fintrack.core.ui.shortcut

import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.WalletId

// FR-039, S-03, S-04: turns shortcuts into tile rows; a shortcut whose wallet is unknown shows an empty name
public object ShortcutTileMapping {
    public fun toTileParams(
        entries: List<QuickEntry>,
        walletNames: Map<WalletId, String>,
    ): List<ShortcutTileParams> =
        entries.map { entry ->
            ShortcutTileParams(
                quickEntryId = entry.quickEntryId,
                title = entry.template.title,
                amount = entry.template.amount,
                walletName = walletNames[entry.walletId].orEmpty(),
            )
        }
}
