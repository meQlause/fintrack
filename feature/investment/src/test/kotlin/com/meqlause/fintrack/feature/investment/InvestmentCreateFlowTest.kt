package com.meqlause.fintrack.feature.investment

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.usecase.CreateInvestmentUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveInvestmentPortfolioUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeValuationRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.Instant

// FR-170, S-08: the creation sheet of InvestmentViewModel from the first key to the stored account
@OptIn(ExperimentalCoroutinesApi::class)
class InvestmentCreateFlowTest {
    private val dispatcher = StandardTestDispatcher()
    private val clock = FakeClock(Instant.parse("2026-11-23T02:00:00Z"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    private fun TestScope.openedViewModel(): InvestmentViewModel {
        val model =
            InvestmentViewModel(
                observePortfolio = ObserveInvestmentPortfolioUseCase(wallets, transactions, FakeValuationRepository()),
                createInvestment = CreateInvestmentUseCase(wallets, WalletNameValidator()),
                clock = clock,
            )
        backgroundScope.launch { model.state.collect {} }
        return model
    }

    /** FR-170: a named account with a kind and an amount is stored and the sheet closes. */
    @Test
    fun `given a filled sheet when saved then the account is stored and the sheet closes (FR-170)`() =
        runTest(dispatcher) {
            val model = openedViewModel()
            model.startCreate()
            model.changeName("Reksa Dana")
            model.openKindPicker(true)
            model.pickKind(InvestKind.MUTUAL_FUND)
            "500".forEach { model.typeDigit(it.toString()) }
            model.backspace()
            model.save()
            advanceUntilIdle()
            assertThat(model.state.value.draft).isNull()
            assertThat(
                model.state.value.portfolio
                    ?.accounts
                    .orEmpty(),
            ).hasSize(1)
        }

    /** FR-170: without a kind nothing is stored; a blank name is rejected and keeps the sheet open. */
    @Test
    fun `given a sheet without a kind or a name when saved then nothing is stored (FR-170)`() =
        runTest(dispatcher) {
            val model = openedViewModel()
            model.save()
            model.startCreate()
            model.save()
            model.pickKind(InvestKind.MUTUAL_FUND)
            model.save()
            advanceUntilIdle()
            assertThat(
                model.state.value.draft
                    ?.nameRejected,
            ).isEqualTo(true)
            model.changeName("Emas")
            advanceUntilIdle()
            assertThat(
                model.state.value.draft
                    ?.nameRejected,
            ).isEqualTo(false)
            model.dismissDraft()
            advanceUntilIdle()
            assertThat(model.state.value.draft).isNull()
            assertThat(
                model.state.value.portfolio
                    ?.accounts
                    .orEmpty(),
            ).hasSize(0)
        }

    /** FR-170: the kind picker opens and closes, and keys do nothing while no sheet is open. */
    @Test
    fun `given no sheet when keys and pickers are used then nothing changes (FR-170)`() =
        runTest(dispatcher) {
            val model = openedViewModel()
            model.typeDigit("5")
            model.backspace()
            model.changeName("Tidak ada")
            model.openKindPicker(true)
            advanceUntilIdle()
            assertThat(model.state.value.draft).isNull()
            model.startCreate()
            model.openKindPicker(true)
            advanceUntilIdle()
            assertThat(
                model.state.value.draft
                    ?.kindPickerOpen ?: false,
            ).isTrue()
            model.openKindPicker(false)
            advanceUntilIdle()
            assertThat(
                model.state.value.draft
                    ?.kindPickerOpen ?: true,
            ).isFalse()
        }
}
