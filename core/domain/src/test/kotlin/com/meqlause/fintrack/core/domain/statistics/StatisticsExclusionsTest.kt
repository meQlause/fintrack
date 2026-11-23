package com.meqlause.fintrack.core.domain.statistics

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.budget.BudgetSpending
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.domain.usecase.BuildPeriodStatisticsUseCase
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.LoanId
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
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// BR-039, BR-040, BR-052: bill payments, loan movements and investment flows never reach statistics or budgets
class StatisticsExclusionsTest {
    private val zone: ZoneId = ZoneId.of("Asia/Jakarta")
    private val november = Period(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30))
    private val noon3Nov: Instant =
        LocalDate
            .of(2026, 11, 3)
            .atTime(12, 0)
            .atZone(zone)
            .toInstant()
    private val statistics = BuildPeriodStatisticsUseCase()
    private val overallBudget =
        Budget(BudgetId(1L), null, Money(1_000_000L), true, Instant.EPOCH, Instant.EPOCH)

    /** BR-039: paying a credit wallet is a TRANSFER from a non-credit wallet, so it is never an expense */
    @Test
    fun `given a bill payment when statistics and budget are built then it contributes nothing (BR-039)`() {
        val payment = transaction(TransactionType.TRANSFER, 500_000L, targetWalletId = 2L)
        assertNothingCounted(listOf(payment))
    }

    /** BR-040: LEND, BORROW, COLLECT and REPAY change balances but never count as income or expense */
    @Test
    fun `given every loan movement when statistics and budget are built then none of them count (BR-040)`() {
        val loanMovements =
            listOf(
                transaction(TransactionType.LEND, 300_000L, loanId = 9L),
                transaction(TransactionType.BORROW, 400_000L, loanId = 10L),
                transaction(TransactionType.COLLECT, 100_000L, loanId = 9L),
                transaction(TransactionType.REPAY, 150_000L, loanId = 10L),
            )
        assertNothingCounted(loanMovements)
    }

    /** BR-040, BR-052: moving money into or out of an investment wallet is a TRANSFER, never spending */
    @Test
    fun `given investment transfers in and out when built then they contribute nothing (BR-052)`() {
        val investmentFlows =
            listOf(
                transaction(TransactionType.TRANSFER, 700_000L, targetWalletId = 5L),
                transaction(TransactionType.TRANSFER, 200_000L, walletId = 5L, targetWalletId = 1L),
            )
        assertNothingCounted(investmentFlows)
    }

    /**
     * BR-052: investment gains are InvestmentValuation values, not Transactions, so they cannot reach
     * this input; a real expense next to every excluded movement must be the only amount counted
     */
    @Test
    fun `given one expense among all excluded movements when built then only that expense is counted`() {
        val mixed =
            listOf(
                transaction(TransactionType.EXPENSE, 100_000L, categoryId = 1L),
                transaction(TransactionType.TRANSFER, 500_000L, targetWalletId = 2L),
                transaction(TransactionType.LEND, 300_000L, loanId = 9L),
                transaction(TransactionType.COLLECT, 100_000L, loanId = 9L),
                transaction(TransactionType.TRANSFER, 700_000L, targetWalletId = 5L),
            )
        val expense = statistics(mixed, TransactionType.EXPENSE, november, 1, zone)
        assertThat(expense.total).isEqualTo(Money(100_000L))
        assertThat(expense.categories.map { it.categoryId }).isEqualTo(listOf(CategoryId(1L)))
        assertThat(BudgetSpending.spentIn(overallBudget, mixed, november, zone)).isEqualTo(Money(100_000L))
    }

    private fun assertNothingCounted(movements: List<Transaction>) {
        val expense = statistics(movements, TransactionType.EXPENSE, november, 1, zone)
        val income = statistics(movements, TransactionType.INCOME, november, 1, zone)
        assertThat(expense.total).isEqualTo(Money(0L))
        assertThat(expense.categories).isEmpty()
        assertThat(income.total).isEqualTo(Money(0L))
        assertThat(income.categories).isEmpty()
        assertThat(BudgetSpending.spentIn(overallBudget, movements, november, zone)).isEqualTo(Money(0L))
    }

    private fun transaction(
        type: TransactionType,
        amount: Long,
        walletId: Long = 1L,
        targetWalletId: Long? = null,
        categoryId: Long? = null,
        loanId: Long? = null,
    ): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type = type, amount = Money(amount)),
            routing =
                Routing(
                    walletId = WalletId(walletId),
                    targetWalletId = targetWalletId?.let { WalletId(it) },
                    categoryId = categoryId?.let { CategoryId(it) },
                    loanId = loanId?.let { LoanId(it) },
                ),
            memo = Memo(),
            origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
            timing = Timing(occurredAt = noon3Nov, audit = Audit(Instant.EPOCH, Instant.EPOCH)),
        )
}
