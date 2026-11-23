package com.meqlause.fintrack.feature.transaction.detail

// S-06: loaded is false until the first lists arrive; closed means the screen should leave (deleted or duplicated)
internal data class TransactionDetailUiState(
    val isLoaded: Boolean = false,
    val detail: TransactionDetail? = null,
    val isDeletePending: Boolean = false,
    val isClosed: Boolean = false,
)
