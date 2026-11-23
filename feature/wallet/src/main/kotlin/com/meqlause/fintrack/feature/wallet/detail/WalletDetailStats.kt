package com.meqlause.fintrack.feature.wallet.detail

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import java.time.Clock
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// FR-017, S-19: this month's Masuk, Keluar and count of one wallet plus the spending of each day for the bar chart
internal data class WalletDetailStats(
    val inflow: Money,
    val outflow: Money,
    val count: Int,
    val perDay: List<Long>,
    val today: Int,
    val monthLabel: String = "",
) {
    companion object {
        val EMPTY =
            WalletDetailStats(inflow = Money(0L), outflow = Money(0L), count = 0, perDay = emptyList(), today = 0)

        // S-19: money counts as incoming when it lands on the wallet (income, borrowed, collected, transfer in)
        fun of(
            walletId: WalletId,
            related: List<Transaction>,
            clock: Clock,
        ): WalletDetailStats {
            val today = LocalDate.now(clock)
            val month = YearMonth.from(today)
            val inMonth = related.filter { YearMonth.from(it.timing.occurredAt.atZone(clock.zone)) == month }
            val (incoming, outgoing) = inMonth.partition { it.isIncoming(walletId) }
            val perDay = LongArray(month.lengthOfMonth())
            outgoing.forEach {
                perDay[
                    it.timing.occurredAt
                        .atZone(clock.zone)
                        .dayOfMonth - 1,
                ] += it.movement.amount.rupiah
            }
            return WalletDetailStats(
                inflow = Money(incoming.sumOf { it.movement.amount.rupiah }),
                outflow = Money(outgoing.sumOf { it.movement.amount.rupiah }),
                count = inMonth.size,
                perDay = perDay.toList(),
                today = today.dayOfMonth,
                monthLabel = month.month.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("id-ID")),
            )
        }

        private fun Transaction.isIncoming(walletId: WalletId): Boolean =
            routing.targetWalletId == walletId ||
                (routing.walletId == walletId && movement.type in INCOMING_TYPES)

        private val INCOMING_TYPES = setOf(TransactionType.INCOME, TransactionType.BORROW, TransactionType.COLLECT)
    }
}
