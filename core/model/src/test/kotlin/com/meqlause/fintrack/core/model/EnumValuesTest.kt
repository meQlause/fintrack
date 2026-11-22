package com.meqlause.fintrack.core.model

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

/** DOC-02: enums hold exactly the values of the domain model, stored by name. */
class EnumValuesTest {
    /** ADR-016: loan movements are transaction types next to income and expense. */
    @Test
    fun `given transaction types when listed then loan movements are included (ADR-016)`() {
        assertThat(TransactionType.entries.map { it.name }).isEqualTo(
            listOf("EXPENSE", "INCOME", "TRANSFER", "LEND", "BORROW", "COLLECT", "REPAY"),
        )
    }

    /** BR-031: the two credit wallet types are CREDIT_CARD and PAYLATER. */
    @Test
    fun `given wallet types when filtered by credit then card and paylater remain (BR-031)`() {
        val creditTypes = WalletType.entries.filter { it == WalletType.CREDIT_CARD || it == WalletType.PAYLATER }

        assertThat(creditTypes).isEqualTo(listOf(WalletType.CREDIT_CARD, WalletType.PAYLATER))
    }

    /** BR-041: a loan is open, settled or written off. */
    @Test
    fun `given loan statuses when listed then they match the loan lifecycle (BR-041)`() {
        assertThat(LoanStatus.entries).isEqualTo(listOf(LoanStatus.OPEN, LoanStatus.SETTLED, LoanStatus.WRITTEN_OFF))
    }

    /** BR-020: the budget bands are normal, warning and exceeded. */
    @Test
    fun `given budget states when listed then the three bands are present (BR-020)`() {
        assertThat(BudgetState.entries).isEqualTo(listOf(BudgetState.NORMAL, BudgetState.WARNING, BudgetState.EXCEEDED))
    }
}
