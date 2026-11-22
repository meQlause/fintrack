package com.meqlause.fintrack.core.ui.transaction

// DOC-09: tapping a row opens the transaction detail
data class TransactionRowCallbacks(
    val onClick: () -> Unit,
)
