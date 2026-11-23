package com.meqlause.fintrack.feature.transaction.edit.navigation

import kotlinx.serialization.Serializable

// DOC-09, FR-030, FR-034, FR-039: the add or edit sheet (S-04); a null transactionId opens an empty sheet,
// a quickEntryId opens it pre-filled from that shortcut (still editable, BR-047)
@Serializable
public data class EditTransactionDestination(
    public val transactionId: Long? = null,
    public val quickEntryId: Long? = null,
)
