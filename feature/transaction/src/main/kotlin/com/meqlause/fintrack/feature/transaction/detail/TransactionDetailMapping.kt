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
    val local = LocalDateTime.ofInstant(timing.occurredAt, clock.zone)
    val categoryName = routing.categoryId?.let { categoryNames[it] }
    val fromName = walletNames[routing.walletId].orEmpty()
    val toName = routing.targetWalletId?.let { walletNames[it] }
    return TransactionDetail(
        type = movement.type,
        amount = movement.amount,
        feeAmount = movement.feeAmount,
        title = memo.merchant.ifBlank { categoryName.orEmpty() },
        merchant = memo.merchant,
        note = memo.note,
        categoryName = categoryName,
        walletLabel = toName?.let { "$fromName → $it" } ?: fromName,
        dateLabel = DateFormatter.dayLabel(local.toLocalDate(), clock),
        timeLabel = DateFormatter.timeLabel(local.toLocalTime()),
        source = origin.source,
        isLoanLinked = routing.loanId != null,
        appearance = RowAppearance.of(movement.type, routing.categoryId?.let(appearances::get)),
    )
}
