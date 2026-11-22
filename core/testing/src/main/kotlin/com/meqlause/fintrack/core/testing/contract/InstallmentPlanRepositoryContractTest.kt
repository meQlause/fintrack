package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.testing.data.aCategory
import com.meqlause.fintrack.core.testing.data.aTransaction
import com.meqlause.fintrack.core.testing.data.aWallet
import com.meqlause.fintrack.core.testing.data.anInstallmentPlan
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test
import java.time.YearMonth

/** DOC-03, FR-140: the contract of InstallmentPlanRepository, run against the fake and the Local implementation. */
abstract class InstallmentPlanRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a fresh store has no instalment plans. */
    @Test
    fun givenNoInstalmentPlansWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.installmentPlans.observeAll().first()).isEmpty()
        }

    /** FR-140, BR-038: a plan with an admin fee keeps its purchase, schedule and fee. */
    @Test
    fun givenPlanWithAdminFeeWhenObservedThenEveryFieldIsKept() =
        runTest {
            val creditWallet = fixture.wallets.insert(aWallet(name = "Kartu"))
            val category = fixture.categories.insert(aCategory())
            val purchaseId =
                fixture.transactions.insert(
                    aTransaction(TransactionType.EXPENSE, creditWallet, amount = 1_200_000L, categoryId = category),
                )
            val feeId =
                fixture.transactions.insert(
                    aTransaction(TransactionType.EXPENSE, creditWallet, amount = 25_000L, categoryId = category),
                )
            val plan = anInstallmentPlan(purchaseId, creditWallet, adminFeeTransactionId = feeId)

            val installmentPlanId = fixture.installmentPlans.insert(plan)

            assertThat(fixture.installmentPlans.observeAll().first())
                .isEqualTo(listOf(plan.copy(installmentPlanId = installmentPlanId)))
        }

    /** BR-037: an early payoff keeps its payoff month and the paid-off status. */
    @Test
    fun givenEarlyPayoffWhenObservedThenThePayoffMonthAndStatusAreKept() =
        runTest {
            val creditWallet = fixture.wallets.insert(aWallet(name = "Kartu"))
            val category = fixture.categories.insert(aCategory())
            val purchaseId =
                fixture.transactions.insert(
                    aTransaction(TransactionType.EXPENSE, creditWallet, amount = 600_000L, categoryId = category),
                )
            val plan =
                anInstallmentPlan(purchaseId, creditWallet, earlyPayoffMonth = YearMonth.parse("2027-03")).let {
                    it.copy(status = InstallmentStatus.PAID_OFF)
                }

            val installmentPlanId = fixture.installmentPlans.insert(plan)

            assertThat(fixture.installmentPlans.observeAll().first())
                .isEqualTo(listOf(plan.copy(installmentPlanId = installmentPlanId)))
        }
}
