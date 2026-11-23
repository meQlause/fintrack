package com.meqlause.fintrack.core.ui.transaction

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.TransactionType

// DOC-08: the row icon and colour: the category, the transfer icon for transfers, the person icon for loans
object RowAppearance {
    fun forType(
        type: TransactionType,
        categoryAppearance: Appearance?,
    ): Appearance =
        when (type) {
            TransactionType.EXPENSE, TransactionType.INCOME -> categoryAppearance ?: Appearance("other", "gray")
            TransactionType.TRANSFER -> Appearance("transfer", "blue")
            else -> Appearance("person", "gray")
        }
}
