package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.Money
import java.time.YearMonth

/** BR-035 to BR-037: portion amounts, billing months and unbilled principal of an instalment plan. Pure. */
public object InstallmentScheduler {
    /** BR-035: portion [number] (1-based); portions before the last are the floor of principal / tenor. */
    public fun portionAmount(
        plan: CreditInstalment,
        number: Int,
    ): Money {
        require(number in 1..plan.tenorMonths) { "portion must be 1..tenor (BR-035)" }
        val base = baseAmount(plan)
        val isLast = number == plan.tenorMonths
        return Money(if (isLast) plan.principal.rupiah - base * (plan.tenorMonths - 1) else base)
    }

    /** BR-035, BR-036, BR-037: every portion in order with the statement that bills it. */
    public fun schedule(plan: CreditInstalment): List<InstalmentPortion> =
        (1..plan.tenorMonths).map { number ->
            InstalmentPortion(number, billingMonth(plan, number), portionAmount(plan, number))
        }

    /** BR-036: principal billed on statements up to and including [month], early payoff included (BR-037). */
    public fun billedThrough(
        plan: CreditInstalment,
        month: YearMonth,
    ): Money =
        schedule(plan)
            .filter { !it.statementMonth.isAfter(month) }
            .fold(Money(0L)) { total, portion -> total + portion.amount }

    /** BR-036: principal not yet billed on statements up to [month]. */
    public fun remainingPrincipal(
        plan: CreditInstalment,
        month: YearMonth,
    ): Money = plan.principal - billedThrough(plan, month)

    /** BR-036: unbilled principal at the cut-off of [month]; a plan bought after that cut-off counts as zero. */
    public fun unbilledAtCutOff(
        plan: CreditInstalment,
        month: YearMonth,
        statementDay: Int,
    ): Money {
        val cutOff = StatementDates.cutOff(month, statementDay)
        return if (plan.purchaseDate.isAfter(cutOff)) Money(0L) else remainingPrincipal(plan, month)
    }

    /** BR-036, BR-037: portion k bills on firstStatementMonth + (k - 1), but never later than the payoff month. */
    private fun billingMonth(
        plan: CreditInstalment,
        number: Int,
    ): YearMonth {
        val scheduled = plan.firstStatementMonth.plusMonths((number - 1).toLong())
        return plan.earlyPayoffMonth?.let { minOf(scheduled, it) } ?: scheduled
    }

    /** BR-035: the floor of principal divided by tenor, shared by every portion except the last. */
    private fun baseAmount(plan: CreditInstalment): Long = plan.principal.rupiah / plan.tenorMonths
}
