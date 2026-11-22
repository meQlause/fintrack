package com.meqlause.fintrack.core.domain.credit

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Money
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.YearMonth

/** BR-035 to BR-037: portion rounding, tenor limits, billing months, early payoff and the purchase cut-off rule. */
class InstallmentSchedulerTest {
    private val november = YearMonth.of(2026, 11)
    private val december = YearMonth.of(2026, 12)
    private val statementDay = 25

    /** BR-035: 1.000.000 over 3 gives 333.333, 333.333 and the remainder 333.334 on the last portion. */
    @Test
    fun `given 1000000 over 3 months when scheduled then last portion takes the remainder (BR-035)`() {
        val plan = CreditFixtures.plan(LocalDate.of(2026, 11, 1), 1_000_000L, tenor = 3, first = november)

        val amounts = InstallmentScheduler.schedule(plan).map { it.amount.rupiah }

        assertThat(amounts).containsExactly(333_333L, 333_333L, 333_334L)
    }

    /** BR-035: portions always add up to the principal exactly, for every allowed tenor. */
    @Test
    fun `given any tenor from 2 to 36 when scheduled then portions sum to the principal (BR-035)`() {
        for (tenor in 2..36) {
            val plan = CreditFixtures.plan(LocalDate.of(2026, 11, 1), 1_000_001L, tenor = tenor, first = november)

            val total = InstallmentScheduler.schedule(plan).fold(0L) { sum, portion -> sum + portion.amount.rupiah }

            assertThat(total).isEqualTo(1_000_001L)
        }
    }

    /** BR-035: every portion before the last one is the floor of principal divided by tenor. */
    @Test
    fun `given 1000001 over 7 months when scheduled then only the last portion differs (BR-035)`() {
        val plan = CreditFixtures.plan(LocalDate.of(2026, 11, 1), 1_000_001L, tenor = 7, first = november)

        val amounts = InstallmentScheduler.schedule(plan).map { it.amount.rupiah }

        assertThat(amounts.dropLast(1).distinct()).containsExactly(142_857L)
        assertThat(amounts.last()).isEqualTo(142_859L)
    }

    /** BR-035, FR-140: tenor 2 is the smallest allowed plan; the second portion lands one statement later. */
    @Test
    fun `given tenor 2 when scheduled then two portions bill on consecutive statements (BR-036)`() {
        val plan = CreditFixtures.plan(LocalDate.of(2026, 11, 22), 1_000_000L, tenor = 2, first = november)

        val portions = InstallmentScheduler.schedule(plan)

        assertThat(portions.map { it.statementMonth }).containsExactly(november, december)
        assertThat(portions.map { it.amount }).containsExactly(Money(500_000L), Money(500_000L))
    }

    /** BR-035, FR-140: tenor 36 is the largest allowed plan; the last portion bills 35 statements after the first. */
    @Test
    fun `given tenor 36 when scheduled then last portion bills 35 statements after the first (BR-036)`() {
        val plan = CreditFixtures.plan(LocalDate.of(2026, 11, 22), 3_600_000L, tenor = 36, first = november)

        val portions = InstallmentScheduler.schedule(plan)

        assertThat(portions.size).isEqualTo(36)
        assertThat(portions.last().statementMonth).isEqualTo(YearMonth.of(2029, 10))
        assertThat(portions.last().amount).isEqualTo(Money(100_000L))
    }

    /** FR-140: tenors outside 2..36 cannot be created. */
    @Test
    fun `given tenor 1 or 37 when creating a plan then it is rejected (FR-140)`() {
        assertThrows<IllegalArgumentException> {
            CreditFixtures.plan(LocalDate.of(2026, 11, 1), 1_000_000L, tenor = 1, first = november)
        }
        assertThrows<IllegalArgumentException> {
            CreditFixtures.plan(LocalDate.of(2026, 11, 1), 1_000_000L, tenor = 37, first = november)
        }
    }

    /** BR-035: asking for a portion outside 1..tenor is an error. */
    @Test
    fun `given portion 0 when asking for its amount then it is rejected (BR-035)`() {
        val plan = CreditFixtures.plan(LocalDate.of(2026, 11, 1), 1_000_000L, tenor = 3, first = november)

        assertThrows<IllegalArgumentException> { InstallmentScheduler.portionAmount(plan, 0) }
    }

    /** BR-036: the worked example from doc 18 leaves 2.500.000 unbilled at the November cut-off. */
    @Test
    fun `given phone 6x plan bought 22 Nov when cut-off 25 Nov then 2500000 is unbilled (BR-036)`() {
        val plan = CreditFixtures.plan(LocalDate.of(2026, 11, 22), 3_000_000L, tenor = 6, first = november)

        val unbilled = InstallmentScheduler.unbilledAtCutOff(plan, november, statementDay)

        assertThat(unbilled).isEqualTo(Money(2_500_000L))
    }

    /** BR-036: a purchase after the cut-off is not part of that statement at all. */
    @Test
    fun `given purchase 26 Nov when cut-off 25 Nov then nothing is unbilled for that statement (BR-036)`() {
        val plan = CreditFixtures.plan(LocalDate.of(2026, 11, 26), 3_000_000L, tenor = 6, first = december)

        val unbilled = InstallmentScheduler.unbilledAtCutOff(plan, november, statementDay)

        assertThat(unbilled.rupiah).isEqualTo(0L)
    }

    /** BR-036: from the next cut-off on, the plan counts with its first portion already billed. */
    @Test
    fun `given purchase 26 Nov when cut-off 25 Dec then first portion is billed and rest unbilled (BR-036)`() {
        val plan = CreditFixtures.plan(LocalDate.of(2026, 11, 26), 3_000_000L, tenor = 6, first = december)

        val unbilled = InstallmentScheduler.unbilledAtCutOff(plan, december, statementDay)

        assertThat(unbilled).isEqualTo(Money(2_500_000L))
    }

    /** BR-037: early payoff in December bills all remaining portions on that statement at once. */
    @Test
    fun `given early payoff in December when scheduled then remaining portions bill in December (BR-037)`() {
        val plan =
            CreditFixtures
                .plan(LocalDate.of(2026, 11, 22), 3_000_000L, tenor = 6, first = november)
                .copy(earlyPayoffMonth = december)

        val portions = InstallmentScheduler.schedule(plan)

        assertThat(portions.map { it.statementMonth }).containsExactly(
            november,
            december,
            december,
            december,
            december,
            december,
        )
        assertThat(portions.sumOf { it.amount.rupiah }).isEqualTo(3_000_000L)
    }

    /** BR-037: after the payoff statement nothing remains unbilled. */
    @Test
    fun `given early payoff in December when cut-off December then nothing is unbilled (BR-037)`() {
        val plan =
            CreditFixtures
                .plan(LocalDate.of(2026, 11, 22), 3_000_000L, tenor = 6, first = november)
                .copy(earlyPayoffMonth = december)

        val unbilled = InstallmentScheduler.unbilledAtCutOff(plan, december, statementDay)

        assertThat(unbilled.rupiah).isEqualTo(0L)
    }

    /** BR-037: before the payoff statement the schedule is unchanged, so November still bills one portion. */
    @Test
    fun `given early payoff in December when cut-off November then only first portion is billed (BR-037)`() {
        val plan =
            CreditFixtures
                .plan(LocalDate.of(2026, 11, 22), 3_000_000L, tenor = 6, first = november)
                .copy(earlyPayoffMonth = december)

        val unbilled = InstallmentScheduler.unbilledAtCutOff(plan, november, statementDay)

        assertThat(unbilled).isEqualTo(Money(2_500_000L))
    }
}
