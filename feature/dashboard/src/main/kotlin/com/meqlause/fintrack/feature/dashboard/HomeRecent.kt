package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionType

// FR-043: one of the five most recent transactions; title is null when the transaction has no text at all
internal data class HomeRecent(
    val transactionId: TransactionId,
    val title: String?,
    val amount: Money,
    val type: TransactionType,
    val stamp: Stamp,
    val style: Style = Style(),
) {
    // FR-043: the wallet and the time of day that make up the row subtitle
    data class Stamp(
        val walletName: String,
        val whenLabel: String,
    )

    // FR-043: the category look of the row and whether a notification created it
    data class Style(
        val appearance: Appearance = Appearance("other", "gray"),
        val auto: Boolean = false,
    )
}
