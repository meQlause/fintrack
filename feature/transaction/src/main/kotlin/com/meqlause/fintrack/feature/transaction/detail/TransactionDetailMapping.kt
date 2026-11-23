package com.meqlause.fintrack.feature.transaction.detail

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.format.DateFormatter
import com.meqlause.fintrack.core.ui.transaction.RowAppearance
import java.time.Clock
import java.time.LocalDateTime

// FR-034, S-06: resolves a stored transaction into the names and labels the detail shows, in the clock's zone
internal fun Transaction.toDetail(
    walletNames: Map<WalletId, String>,
    categoryNames: Map<CategoryId, String>,
    clock: Clock,
    appearances: Map<CategoryId, Appearance> = emptyMap(),
): TransactionDetail {
    val categoryName = routing.categoryId?.let { categoryNames[it] }
    return TransactionDetail(
        type = movement.type,
        money = DetailMoney(amount = movement.amount, feeAmount = movement.feeAmount),
        text = DetailText(memo.merchant.ifBlank { categoryName.orEmpty() }, memo.merchant, memo.note, categoryName),
        place = placeOf(walletNames, clock),
        origin =
            DetailOrigin(
                source = origin.source,
                isLoanLinked = routing.loanId != null,
                appearance = RowAppearance.forType(movement.type, routing.categoryId?.let(appearances::get)),
            ),
    )
}

// FR-034: "Dari → Ke" for a transfer, the one wallet otherwise, with the date and time in the clock's zone
private fun Transaction.placeOf(
    walletNames: Map<WalletId, String>,
    clock: Clock,
): DetailPlace {
    val local = LocalDateTime.ofInstant(timing.occurredAt, clock.zone)
    val fromName = walletNames[routing.walletId].orEmpty()
    val toName = routing.targetWalletId?.let { walletNames[it] }
    return DetailPlace(
        walletLabel = toName?.let { "$fromName → $it" } ?: fromName,
        dateLabel = DateFormatter.dayLabel(local.toLocalDate(), clock),
        timeLabel = DateFormatter.timeLabel(local.toLocalTime()),
    )
}
