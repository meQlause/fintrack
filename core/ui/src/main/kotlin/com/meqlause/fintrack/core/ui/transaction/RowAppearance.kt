package com.meqlause.fintrack.core.ui.transaction

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.TransactionType

// DOC-08: the icon and colour a transaction row shows: its category, transfers use the transfer icon, loans the person icon
fun rowAppearanceOf(
    type: TransactionType,
    categoryAppearance: Appearance?,
): Appearance =
    when (type) {
        TransactionType.EXPENSE, TransactionType.INCOME -> categoryAppearance ?: Appearance("other", "gray")
        TransactionType.TRANSFER -> Appearance("transfer", "blue")
        else -> Appearance("person", "gray")
    }
