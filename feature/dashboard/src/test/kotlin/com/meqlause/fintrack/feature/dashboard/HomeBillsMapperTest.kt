package com.meqlause.fintrack.feature.dashboard

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.bills.BillsCard
import com.meqlause.fintrack.core.domain.bills.CreditBill
import com.meqlause.fintrack.core.domain.credit.StatementAmounts
import com.meqlause.fintrack.core.domain.credit.StatementPeriod
import com.meqlause.fintrack.core.domain.credit.StatementSummary
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.time.FakeClock
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

// FR-137, FR-146: the Tagihan rows show what is still due, or zero before the statement is issued
class HomeBillsMapperTest {
    private val clock = FakeClock(Instant.parse("2026-11-15T03:00:00Z"))
    private val period =
        StatementPeriod(
            month = YearMonth.of(2026, 11),
            cutOff = LocalDate.of(2026, 11, 20),
            periodStart = LocalDate.of(2026, 10, 21),
            dueDate = LocalDate.of(2026, 12, 5),
        )

    // FR-137: a statement not yet issued shows zero, whatever the amounts say
    @Test
    fun givenNotIssuedStatementWhenMappedThenAmountIsZero() {
        val bill = bill(StatementStatus.NOT_ISSUED, outstanding = 500_000L, remaining = 500_000L)

        assertThat(mappedBill(bill).amount).isEqualTo(Money(0L))
    }

    // FR-137: a paid statement with nothing left shows the whole statement amount
    @Test
    fun givenPaidStatementWhenMappedThenWholeStatementIsShown() {
        val bill = bill(StatementStatus.PAID, outstanding = 300_000L, remaining = 0L)

        assertThat(mappedBill(bill).amount).isEqualTo(Money(300_000L))
        assertThat(mappedBill(bill).status).isEqualTo(StatementStatus.PAID)
    }

    // FR-137: a due statement with a balance left shows only the remaining due
    @Test
    fun givenDueStatementWhenMappedThenRemainingDueIsShown() {
        val bill = bill(StatementStatus.DUE, outstanding = 800_000L, remaining = 250_000L)

        assertThat(mappedBill(bill).amount).isEqualTo(Money(250_000L))
    }

    // FR-137: the instalments due this period are carried over unchanged
    @Test
    fun givenCardWhenMappedThenInstalmentsDueIsKept() {
        val card = BillsCard(listOf(bill(StatementStatus.OVERDUE, 1L, 1L)), Money(75_000L))
        val mapped = HomeBillsMapper.toHomeBills(card, clock)

        assertThat(mapped.instalmentsDue).isEqualTo(Money(75_000L))
        assertThat(mapped.bills.single().walletId).isEqualTo(WalletId(1L))
    }

    private fun mappedBill(bill: CreditBill): HomeBill =
        HomeBillsMapper.toHomeBills(BillsCard(listOf(bill), Money(0L)), clock).bills.single()

    private fun bill(
        status: StatementStatus,
        outstanding: Long,
        remaining: Long,
    ): CreditBill {
        val amounts = StatementAmounts(Money(outstanding), Money(0L), Money(outstanding - remaining))
        return CreditBill(
            walletId = WalletId(1L),
            walletName = "BCA Kredit",
            statement = StatementSummary(period = period, breakdown = amounts, status = status),
        )
    }
}
