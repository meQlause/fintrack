package com.meqlause.fintrack.feature.credit.instalment

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.usecase.DeleteInstalmentPlanUseCase
import com.meqlause.fintrack.core.domain.usecase.PayOffInstalmentUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CreditTerms
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
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeInstallmentPlanRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

// FR-142, FR-144, FR-145, S-16: the instalment list and detail view models against the in-memory fakes
@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InstalmentViewModelsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = FakeClock(Instant.parse("2026-11-05T03:00:00Z"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val plans = FakeInstallmentPlanRepository()

    private suspend fun card(): WalletId =
        wallets.insert(
            Wallet(
                walletId = WalletId(0L),
                kind = WalletKind(WalletType.CREDIT_CARD, investKind = null),
                terms = WalletTerms(Money(0L), CreditTerms(Money(10_000_000L), statementDay = 25, dueDay = 10)),
                profile = WalletProfile("BCA Visa", Appearance("card", "slate"), sortOrder = 0),
                status = WalletStatus(),
                audit = Audit(Instant.EPOCH, Instant.EPOCH),
            ),
        )

    private suspend fun plan(
        card: WalletId,
        tenor: Int,
        first: String = "2026-10",
    ): Long {
        val purchase =
            transactions.insert(
                Transaction(
                    transactionId = TransactionId(0L),
                    movement = Movement(TransactionType.EXPENSE, Money(1_200_000L)),
                    routing = Routing(card, null, categoryId = null, loanId = null),
                    memo = Memo(),
                    origin = Origin(TransactionSource.MANUAL, inboxItemId = null),
                    timing = Timing(Instant.parse("2026-09-10T03:00:00Z"), Audit(Instant.EPOCH, Instant.EPOCH)),
                ),
            )
        return plans
            .insert(
                InstallmentPlan(
                    installmentPlanId = InstallmentPlanId(0L),
                    purchase = Purchase(purchase, card, "Laptop", Money(1_200_000L), adminFeeTransactionId = null),
                    schedule = Schedule(tenor, YearMonth.parse(first), earlyPayoffMonth = null),
                    status = InstallmentStatus.ACTIVE,
                    audit = Audit(Instant.EPOCH, Instant.EPOCH),
                ),
            ).value
    }

    private fun TestScope.detailOf(planKey: Long): InstalmentDetailViewModel {
        val model =
            InstalmentDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("planId" to planKey)),
                sources = InstalmentSources(plans, transactions, wallets),
                actions =
                    InstalmentActions(
                        PayOffInstalmentUseCase(plans, wallets, clock),
                        DeleteInstalmentPlanUseCase(plans, transactions, PassthroughAtomicRunner()),
                    ),
                clock = clock,
            )
        backgroundScope.launch { model.state.collect {} }
        return model
    }

    private fun TestScope.listOf(): InstalmentListViewModel {
        val model = InstalmentListViewModel(plans, transactions, wallets, clock)
        backgroundScope.launch { model.state.collect {} }
        return model
    }

    /** FR-142: a running plan is listed under Aktif and not under Selesai; a finished one the other way round. */
    @Test
    fun `given active and finished plans when the segments are picked then each lists its own (FR-142)`() =
        runTest {
            val cardId = card()
            plan(cardId, tenor = 12)
            plan(cardId, tenor = 2, first = "2026-08")
            val model = listOf()
            advanceUntilIdle()
            val active = model.state.value as InstalmentListUiState.Success
            assertThat(active.rows).hasSize(1)
            model.pickFilter(InstalmentFilter.FINISHED)
            advanceUntilIdle()
            val finished = model.state.value as InstalmentListUiState.Success
            assertThat(finished.rows).hasSize(1)
            assertThat(finished.filter).isEqualTo(InstalmentFilter.FINISHED)
            assertThat(finished.rows.single().isFinished).isTrue()
        }

    /** FR-142: plans of a missing card or a card without terms are left out. */
    @Test
    fun `given a plan on a missing card when listed then it is left out (FR-142)`() =
        runTest {
            plan(WalletId(99L), tenor = 12)
            val model = listOf()
            advanceUntilIdle()
            assertThat((model.state.value as InstalmentListUiState.Success).rows).isEmpty()
        }

    /** FR-144: the detail shows the plan and "Lunasi sekarang" sets the early payoff month. */
    @Test
    fun `given a running plan when paid off then the payoff month is set (FR-144)`() =
        runTest {
            val planKey = plan(card(), tenor = 12)
            val model = detailOf(planKey)
            advanceUntilIdle()
            val shown = model.state.value as InstalmentDetailUiState.Success
            assertThat(shown.instalmentRow.canPayOff).isTrue()
            model.payOff()
            advanceUntilIdle()
            assertThat(
                plans
                    .observeAll()
                    .first()
                    .single()
                    .schedule.earlyPayoffMonth,
            ).isNotNull()
            val after = model.state.value as InstalmentDetailUiState.Success
            assertThat(after.instalmentRow.canPayOff).isFalse()
        }

    /** FR-145: deleting asks first, can be cancelled, and then closes the screen and removes the plan. */
    @Test
    fun `given a plan when delete is asked cancelled and confirmed then it is removed (FR-145)`() =
        runTest {
            val planKey = plan(card(), tenor = 12)
            val model = detailOf(planKey)
            advanceUntilIdle()
            model.requestDelete()
            advanceUntilIdle()
            assertThat((model.state.value as InstalmentDetailUiState.Success).confirmDelete).isTrue()
            model.dismissDelete()
            advanceUntilIdle()
            assertThat((model.state.value as InstalmentDetailUiState.Success).confirmDelete).isFalse()
            model.confirmDelete()
            advanceUntilIdle()
            assertThat(model.state.value).isEqualTo(InstalmentDetailUiState.Closed)
            assertThat(plans.observeAll().first()).isEmpty()
        }

    /** FR-144: an unknown plan id ends in the missing state. */
    @Test
    fun `given an unknown plan when the detail opens then it is missing (FR-144)`() =
        runTest {
            val model = detailOf(404L)
            advanceUntilIdle()
            assertThat(model.state.value).isInstanceOf(InstalmentDetailUiState.Missing::class)
        }

    /** BR-035: the mapped mapping bills the portions up to the latest statement cut-off. */
    @Test
    fun `given a plan when mapped then the portions up to the cut-off count as billed (BR-035)`() =
        runTest {
            val planKey = plan(card(), tenor = 4)
            val stored = plans.observeAll().first().single()
            val mapped =
                stored.toRow(
                    cardName = "BCA Visa",
                    transactions = transactions.observeAll().first(),
                    cutOff = StatementCutOff(statementDay = 25, today = LocalDate.parse("2026-11-26")),
                    zone = java.time.ZoneOffset.UTC,
                )
            assertThat(planKey).isEqualTo(stored.installmentPlanId.value)
            assertThat(mapped?.portions).isNotNull()
            assertThat(mapped?.billed).isEqualTo(2)
            assertThat(mapped?.nextMonth).isEqualTo(YearMonth.parse("2026-12"))
        }
}
