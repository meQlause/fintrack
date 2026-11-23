package com.meqlause.fintrack.core.ui.transaction

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType

// DOC-08: everything one transaction row shows, already formatted as copy; the row holds no state
data class TransactionRowParams(
    val iconKey: String,
    val colorKey: String,
    val title: String,
    val subtitle: String,
    val amount: Money,
    val type: TransactionType,
)
