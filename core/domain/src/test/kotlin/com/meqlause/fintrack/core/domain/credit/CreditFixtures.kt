package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Money
import java.time.LocalDate
import java.time.YearMonth

/** DOC-18 section 2: the worked example used by the statement tests. */
internal object CreditFixtures {
    private const val LIMIT_RUPIAH = 10_000_000L

    /** DOC-18: limit Rp10.000.000, statement day 25, due day 10, phone bought on a 6x plan on 22 Nov. */
    fun workedExample(): CreditAccount =
        CreditAccount(
            terms = CreditTerms(creditLimit = Money(10_000_000L), statementDay = 25, dueDay = 10),
            initialBalance = Money(0L),
            createdOn = LocalDate.of(2026, 1, 1),
            entries =
                listOf(
                    expense(LocalDate.of(2026, 11, 20), 500_000L),
                    expense(LocalDate.of(2026, 11, 22), 3_000_000L),
                    transferIn(LocalDate.of(2026, 12, 1), 1_000_000L),
                ),
            instalments =
                listOf(
                    plan(LocalDate.of(2026, 11, 22), 3_000_000L, tenor = 6, first = YearMonth.of(2026, 11)),
                ),
        )

    /** BR-031: an account with the given terms, opening balance, entries and instalments. */
    fun account(
        createdOn: LocalDate = LocalDate.of(2026, 1, 1),
        entries: List<CreditEntry> = emptyList(),
        instalments: List<CreditInstalment> = emptyList(),
    ): CreditAccount =
        CreditAccount(
            terms = CreditTerms(Money(LIMIT_RUPIAH), statementDay = 25, dueDay = 10),
            initialBalance = Money(0L),
            createdOn = createdOn,
            entries = entries,
            instalments = instalments,
        )

    /** BR-031: a purchase on the card; delta is negative because it increases the debt. */
    fun expense(
        date: LocalDate,
        rupiah: Long,
    ): CreditEntry = CreditEntry(date, Money(-rupiah), isTransferIn = false)

    /** BR-033: a transfer into the card, which reduces the debt. */
    fun transferIn(
        date: LocalDate,
        rupiah: Long,
    ): CreditEntry = CreditEntry(date, Money(rupiah), isTransferIn = true)

    /** BR-035, BR-036: an instalment plan split into tenor portions starting on the first statement. */
    fun plan(
        purchaseDate: LocalDate,
        rupiah: Long,
        tenor: Int,
        first: YearMonth,
    ): CreditInstalment =
        CreditInstalment(
            purchaseDate = purchaseDate,
            principal = Money(rupiah),
            tenorMonths = tenor,
            firstStatementMonth = first,
        )
}
