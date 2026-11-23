package com.meqlause.fintrack.feature.wallet.detail

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.time.FakeClock
import org.junit.Test
import java.time.Instant

// FR-017, S-19: this month's Masuk, Keluar, count and per-day spending of one wallet
class WalletDetailStatsTest {
    private val clock = FakeClock(Instant.parse("2026-11-22T08:00:00Z"))
    private val audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH)
    private val mine = WalletId(1L)
    private val other = WalletId(2L)

    private fun tx(
        type: TransactionType,
        stamp: String,
        rupiah: Long,
        target: WalletId? = null,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = type, amount = Money(rupiah)),
            routing = Routing(walletId = mine, targetWalletId = target, categoryId = null, loanId = null),
            memo = Memo(note = "", merchant = ""),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = Instant.parse("$stamp:00Z"), audit = audit),
        )

    // S-19: income lands as Masuk, expense and a transfer out as Keluar; other months are ignored
    @Test
    fun givenMixedTransactionsWhenSummarisedThenOnlyThisMonthCountsByDirection() {
        val rows =
            listOf(
                tx(TransactionType.INCOME, "2026-11-03T09:00", 1_000L),
                tx(TransactionType.EXPENSE, "2026-11-03T10:00", 300L),
                tx(TransactionType.TRANSFER, "2026-11-20T10:00", 200L, target = other),
                tx(TransactionType.EXPENSE, "2026-10-30T10:00", 999L),
            )

        val stats = WalletDetailStats.of(mine, rows, clock)

        assertThat(stats.inflow).isEqualTo(Money(1_000L))
        assertThat(stats.outflow).isEqualTo(Money(500L))
        assertThat(stats.count).isEqualTo(3)
        assertThat(stats.perDay[2]).isEqualTo(300L)
        assertThat(stats.perDay.size).isEqualTo(30)
        assertThat(stats.today).isEqualTo(22)
    }
}
