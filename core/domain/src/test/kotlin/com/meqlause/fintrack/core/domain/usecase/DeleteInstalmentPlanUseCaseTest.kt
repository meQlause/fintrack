package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Purchase
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Schedule
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.YearMonth

// FR-145, BR-011: deleting an instalment plan removes its purchase and admin fee together with the plan

/** FR-145: delete instalment plan use case tests. */
class DeleteInstalmentPlanUseCaseTest {
    private val transactions = InMemoryTransactionRepository()
    private val plans = InMemoryInstallmentPlanRepository()
    private val useCase = DeleteInstalmentPlanUseCase(plans, transactions, ImmediateAtomicRunner())
    private val card = WalletId(1L)

    /** FR-145: an unknown plan is ignored and nothing is deleted. */
    @Test
    fun givenUnknownPlanWhenDeletedThenNothingIsRemoved() {
        runBlocking {
            transactions.insert(rowOf(TransactionType.EXPENSE))
            useCase(InstallmentPlanId(42L))
            assertThat(transactions.stored).hasSize(1)
        }
    }

    /** FR-145: the purchase, its admin fee and the plan are removed; an unrelated row stays. */
    @Test
    fun givenPlanWithFeeWhenDeletedThenPurchaseFeeAndPlanAreRemoved() {
        runBlocking {
            val purchase = transactions.insert(rowOf(TransactionType.EXPENSE))
            val feeRow = transactions.insert(rowOf(TransactionType.EXPENSE))
            val unrelated = transactions.insert(rowOf(TransactionType.INCOME))
            val planId = plans.insert(plan(purchase, feeRow))
            useCase(planId)
            assertThat(plans.observeAll().first()).isEmpty()
            assertThat(transactions.stored.map { it.transactionId }).containsExactly(unrelated)
        }
    }

    /** FR-145: a plan without an admin fee removes only its purchase and the plan. */
    @Test
    fun givenPlanWithoutFeeWhenDeletedThenOnlyPurchaseAndPlanAreRemoved() {
        runBlocking {
            val purchase = transactions.insert(rowOf(TransactionType.EXPENSE))
            val kept = transactions.insert(rowOf(TransactionType.INCOME))
            val planId = plans.insert(plan(purchase, adminFee = null))
            useCase(planId)
            assertThat(plans.observeAll().first()).isEmpty()
            assertThat(transactions.stored.map { it.transactionId }).containsExactly(kept)
            assertThat(transactions.stored.single().transactionId).isEqualTo(kept)
        }
    }

    private fun rowOf(type: TransactionType): Transaction =
        Transaction(
            transactionId = TransactionId(0L),
            movement = Movement(type, Money(10_000L)),
            routing = Routing(card, null, categoryId = null, loanId = null),
            memo = Memo(),
            origin = Origin(TransactionSource.MANUAL, null),
            timing = Timing(Instant.EPOCH, Audit(Instant.EPOCH, Instant.EPOCH)),
        )

    private fun plan(
        purchase: TransactionId,
        adminFee: TransactionId?,
    ): InstallmentPlan =
        InstallmentPlan(
            installmentPlanId = InstallmentPlanId(0L),
            purchase = Purchase(purchase, card, "Sepatu", Money(300_000L), adminFeeTransactionId = adminFee),
            schedule = Schedule(tenorMonths = 3, firstStatementMonth = YearMonth.of(2026, 11), earlyPayoffMonth = null),
            status = InstallmentStatus.ACTIVE,
            audit = Audit(Instant.EPOCH, Instant.EPOCH),
        )
}
