package com.meqlause.fintrack.core.domain.investment

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import com.meqlause.fintrack.core.model.WalletId
import org.junit.jupiter.api.Test
import java.time.Instant

// BR-051, BR-054: the worked example of 19-investments.md and the same-day withdrawal rule

/** Investment calculator tests. */
class InvestmentCalculatorTest {
    private val oct2 = Instant.parse("2026-10-02T08:00:00Z")
    private val nov2 = Instant.parse("2026-11-02T08:00:00Z")
    private val nov20Morning = Instant.parse("2026-11-20T02:00:00Z")
    private val nov20Noon = Instant.parse("2026-11-20T05:00:00Z")
    private val nov21 = Instant.parse("2026-11-21T02:00:00Z")

    // BR-051: the worked example, two top-ups of 1.000.000 and a value of 2.080.000 on 20 Nov

    /** Given two top ups and an update when valuated then gain is eighty thousand at four point zero percent. */
    @Test
    fun givenTwoTopUpsAndAnUpdateWhenValuatedThenGainIsEightyThousandAtFourPointZeroPercent() {
        val flows =
            listOf(
                InvestmentFlow(oct2, Money(1_000_000L), MoneyDirection.CREDIT),
                InvestmentFlow(nov2, Money(1_000_000L), MoneyDirection.CREDIT),
            )
        val result = InvestmentCalculator.valuate(Money(0L), flows, listOf(valuation(2_080_000L, nov20Morning)))
        assertThat(result.modal).isEqualTo(Money(2_000_000L))
        assertThat(result.currentValue).isEqualTo(Money(2_080_000L))
        assertThat(result.gain).isEqualTo(Money(80_000L))
        assertThat(result.gainTenthPercent).isEqualTo(40L)
    }

    // BR-051: a withdrawal after the update reduces modal and value alike, so the gain is kept

    /** Given withdrawal after update when valuated then gain is still eighty thousand. */
    @Test
    fun givenWithdrawalAfterUpdateWhenValuatedThenGainIsStillEightyThousand() {
        val flows =
            listOf(
                InvestmentFlow(oct2, Money(1_000_000L), MoneyDirection.CREDIT),
                InvestmentFlow(nov2, Money(1_000_000L), MoneyDirection.CREDIT),
                InvestmentFlow(nov21, Money(500_000L), MoneyDirection.DEBIT),
            )
        val result = InvestmentCalculator.valuate(Money(0L), flows, listOf(valuation(2_080_000L, nov20Morning)))
        assertThat(result.modal).isEqualTo(Money(1_500_000L))
        assertThat(result.currentValue).isEqualTo(Money(1_580_000L))
        assertThat(result.gain).isEqualTo(Money(80_000L))
    }

    // BR-051: a same-day withdrawal after the update counts, because the comparison uses full timestamps

    /** Given same day withdrawal after update when valuated then it counts after the update. */
    @Test
    fun givenSameDayWithdrawalAfterUpdateWhenValuatedThenItCountsAfterTheUpdate() {
        val flows = listOf(InvestmentFlow(nov20Noon, Money(500_000L), MoneyDirection.DEBIT))
        val result = InvestmentCalculator.valuate(Money(2_000_000L), flows, listOf(valuation(2_080_000L, nov20Morning)))
        assertThat(result.modal).isEqualTo(Money(1_500_000L))
        assertThat(result.currentValue).isEqualTo(Money(1_580_000L))
    }

    // BR-051: without any valuation the current value is the modal

    /** Given no valuation when valuated then current value equals modal. */
    @Test
    fun givenNoValuationWhenValuatedThenCurrentValueEqualsModal() {
        val result = InvestmentCalculator.valuate(Money(2_000_000L), emptyList(), emptyList())
        assertThat(result.currentValue).isEqualTo(Money(2_000_000L))
        assertThat(result.gain).isEqualTo(Money(0L))
        assertThat(result.lastValuedAt).isNull()
    }

    private fun valuation(
        value: Long,
        valuationTime: Instant,
    ): ValuationEntry =
        ValuationEntry(
            valuationId = ValuationEntryId(1L),
            walletId = WalletId(1L),
            value = Money(value),
            valuedAt = valuationTime,
            createdAt = valuationTime,
        )
}
