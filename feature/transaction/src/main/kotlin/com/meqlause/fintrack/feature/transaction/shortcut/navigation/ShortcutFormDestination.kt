package com.meqlause.fintrack.feature.transaction.shortcut.navigation

import kotlinx.serialization.Serializable

// DOC-09, FR-039: the "Pintasan baru" form (S-03); a null quickEntryId starts blank, otherwise edits that shortcut
@Serializable
public data class ShortcutFormDestination(
    public val quickEntryId: Long? = null,
)
