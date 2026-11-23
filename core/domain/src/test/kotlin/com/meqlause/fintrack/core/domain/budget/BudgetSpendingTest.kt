package com.meqlause.fintrack.core.domain.budget

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.model.TransactionType
import org.junit.jupiter.api.Test
import java.time.LocalDate

// BR-040, FR-074: which transactions count toward a budget in its period
internal class BudgetSpendingTest {
    private val november = Period(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30))

    @Test
    fun `given expenses in two categories when a category budget is spent then only its own expenses count (FR-070)`() {
        val transactions = listOf(aMovement(300L), aMovement(200L, categoryId = CATEGORY_TRANSPORT))
        val spent = BudgetSpending.spentIn(aBudget(), transactions, november, TEST_ZONE)
        assertThat(spent.rupiah).isEqualTo(300L)
    }

    @Test
    fun `given the overall budget when spent then every expense counts (FR-074)`() {
        val transactions = listOf(aMovement(300L), aMovement(200L, categoryId = CATEGORY_TRANSPORT))
        val spent = BudgetSpending.spentIn(aBudget(categoryId = null), transactions, november, TEST_ZONE)
        assertThat(spent.rupiah).isEqualTo(500L)
    }

    @Test
    fun `given transfers, income and loan movements when spent then they never count (BR-040)`() {
        val transactions =
            listOf(
                aMovement(500L, type = TransactionType.TRANSFER),
                aMovement(500L, type = TransactionType.INCOME),
                aMovement(500L, type = TransactionType.LEND),
                aMovement(500L, type = TransactionType.REPAY),
            )
        val spent = BudgetSpending.spentIn(aBudget(categoryId = null), transactions, november, TEST_ZONE)
        assertThat(spent.rupiah).isEqualTo(0L)
    }

    @Test
    fun `given dates on both edges and outside the period when spent then only inclusive dates count (BR-010)`() {
        val transactions =
            listOf(
                aMovement(100L, date = "2026-11-01"),
                aMovement(100L, date = "2026-11-30"),
                aMovement(100L, date = "2026-12-01"),
                aMovement(100L, date = "2026-10-31"),
            )
        val spent = BudgetSpending.spentIn(aBudget(categoryId = null), transactions, november, TEST_ZONE)
        assertThat(spent.rupiah).isEqualTo(200L)
    }
}
