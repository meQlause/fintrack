package com.meqlause.fintrack.feature.transaction.detail

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType

// FR-034, FR-038, S-06: the facts of one transaction, already resolved to names and labels
internal data class TransactionDetail(
    val type: TransactionType,
    val money: DetailMoney,
    val text: DetailText,
    val place: DetailPlace,
    val origin: DetailOrigin,
) {
    val amount: Money get() = money.amount
    val feeAmount: Money get() = money.feeAmount
    val title: String get() = text.title
    val merchant: String get() = text.merchant
    val note: String get() = text.note
    val categoryName: String? get() = text.categoryName
    val walletLabel: String get() = place.walletLabel
    val dateLabel: String get() = place.dateLabel
    val timeLabel: String get() = place.timeLabel
    val source: TransactionSource get() = origin.source
    val isLoanLinked: Boolean get() = origin.isLoanLinked
    val appearance: Appearance get() = origin.appearance
}

// FR-034: the amount and the fee
internal data class DetailMoney(
    val amount: Money,
    val feeAmount: Money,
)

// FR-034: the title, merchant, note and category name
internal data class DetailText(
    val title: String,
    val merchant: String,
    val note: String,
    val categoryName: String?,
)

// FR-034: the wallet, the date and the time as labels
internal data class DetailPlace(
    val walletLabel: String,
    val dateLabel: String,
    val timeLabel: String,
)

// FR-038: where the transaction came from, whether a loan owns it and how its row looks
internal data class DetailOrigin(
    val source: TransactionSource,
    val isLoanLinked: Boolean,
    val appearance: Appearance = Appearance("other", "gray"),
)
