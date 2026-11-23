package com.meqlause.fintrack.feature.transaction.edit.navigation

import kotlinx.serialization.Serializable

// DOC-09, FR-030, FR-034: the add or edit sheet (S-04); a null transactionId opens an empty sheet
@Serializable
public data class EditTransactionDestination(
    public val transactionId: Long? = null,
)
