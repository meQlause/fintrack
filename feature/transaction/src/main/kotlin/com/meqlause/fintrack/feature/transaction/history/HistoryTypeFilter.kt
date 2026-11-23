package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.feature.transaction.R

// FR-037, S-05: the type tabs of Riwayat; EVERY shows all movements, the others one type each
internal enum class HistoryTypeFilter(
    val labelRes: Int,
) {
    EVERY(R.string.history_type_every),
    EXPENSE(R.string.history_type_expense),
    INCOME(R.string.history_type_income),
    TRANSFER(R.string.transaction_type_transfer),
    ;

    // FR-037: true when a transaction of [type] belongs under this tab
    fun accepts(type: TransactionType): Boolean =
        when (this) {
            EVERY -> true
            EXPENSE -> type == TransactionType.EXPENSE
            INCOME -> type == TransactionType.INCOME
            TRANSFER -> type == TransactionType.TRANSFER
        }
}
