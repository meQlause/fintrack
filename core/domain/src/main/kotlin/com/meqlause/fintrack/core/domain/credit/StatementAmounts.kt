package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.Money

/** BR-033: the amounts that make up one statement; the derived values follow the spec formulas. */
public data class StatementAmounts(
    public val outstanding: Money,
    public val unbilledInstalments: Money,
    public val paidSinceCutoff: Money,
) {
    /** BR-033: outstanding at cut-off minus the unbilled instalment principal. */
    public val statementAmount: Money
        get() = outstanding - unbilledInstalments

    /** BR-033: the statement amount minus payments since the cut-off, never below 0. */
    public val remainingDue: Money
        get() = Money(maxOf(0L, statementAmount.rupiah - paidSinceCutoff.rupiah))

    /** FR-133: true when some payment was made since the cut-off and something is still due. */
    public val isPartiallyPaid: Boolean
        get() = paidSinceCutoff.rupiah > 0L && !remainingDue.isZero
}
