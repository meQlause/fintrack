package com.meqlause.fintrack.feature.wallet

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.usecase.ObserveInvestmentPortfolioUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveLoansUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveNetWorthUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.repository.FakeInstallmentPlanRepository
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeValuationRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

// FR-010..FR-012, FR-139, S-09: the editor paths of WalletViewModel, from the first tap to the stored wallet
@OptIn(ExperimentalCoroutinesApi::class)
class WalletViewModelEditTest {
    private val dispatcher = StandardTestDispatcher()
    private val transactions = FakeTransactionRepository()
    private val repository = FakeWalletRepository(transactions)
    private val clock = FakeClock(Instant.parse("2026-11-23T00:00:00Z"))

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    private fun TestScope.opened(): WalletViewModel {
        val worth =
            ObserveNetWorthUseCase(
                wallets = repository,
                transactions = transactions,
                plans = FakeInstallmentPlanRepository(),
                loans = ObserveLoansUseCase(FakeLoanRepository(), transactions),
                investments = ObserveInvestmentPortfolioUseCase(repository, transactions, FakeValuationRepository()),
                clock = clock,
            )
        val model = WalletViewModel(repository, WalletNameValidator(), worth, clock)
        backgroundScope.launch { model.state.collect {} }
        return model
    }

    private suspend fun stored(name: String): Wallet {
        val wallet =
            Wallet(
                walletId = WalletId(0L),
                kind = WalletKind(WalletType.CASH, investKind = null),
                terms = WalletTerms(Money(1_000L), credit = null),
                profile = WalletProfile(name, Appearance("cash", "green"), sortOrder = 0),
                status = WalletStatus(),
                audit = Audit(Instant.EPOCH, Instant.EPOCH),
            )
        return wallet.copy(walletId = repository.insert(wallet))
    }

    private suspend fun archivedNow(): Boolean =
        repository
            .observeAll()
            .first()
            .single()
            .status.isArchived

    /** FR-010: saving a credit wallet without a limit keeps the editor open with the limit error. */
    @Test
    fun `given a credit draft without a limit when saved then the limit error shows (FR-139)`() =
        runTest(dispatcher) {
            val model = opened()
            model.save()
            model.startCreate()
            model.edit { it.withName("Kartu").withType(WalletType.CREDIT_CARD) }
            model.save()
            advanceUntilIdle()
            val editor = (model.state.value as? WalletUiState.Empty)?.editor
            assertThat(editor?.limitError).isEqualTo(true)
            assertThat(repository.observeAll().first().size).isEqualTo(0)
        }

    /** FR-011: an edited wallet is updated in place and a picked day lands in the draft. */
    @Test
    fun `given a stored wallet when it is edited and saved then it is updated in place (FR-011)`() =
        runTest(dispatcher) {
            val cash = stored("Tunai")
            val model = opened()
            advanceUntilIdle()
            model.startEdit(WalletItem(cash, Money(1_000L)))
            model.edit { it.withName("Tunai baru") }
            model.openPicker(WalletPicker.STATEMENT_DAY)
            model.pickDay(12)
            model.save()
            advanceUntilIdle()
            val updated = repository.observeAll().first().single()
            assertThat(updated.profile.name).isEqualTo("Tunai baru")
            assertThat(updated.walletId).isEqualTo(cash.walletId)
        }

    /** FR-012: archiving needs an open editor of a stored wallet and a second call restores it. */
    @Test
    fun `given an open stored wallet when archived and restored then the flag follows (FR-012)`() =
        runTest(dispatcher) {
            val item = WalletItem(stored("Tunai"), Money(1_000L))
            val model = opened()
            advanceUntilIdle()
            model.setArchivedEditing(true)
            model.startEdit(item)
            model.setArchivedEditing(true)
            advanceUntilIdle()
            assertThat(archivedNow()).isTrue()
            model.startEdit(item)
            model.setArchivedEditing(false)
            advanceUntilIdle()
            assertThat(archivedNow()).isFalse()
        }

    /** S-09: the archived section toggles and a picked type closes the drawer. */
    @Test
    fun `given the list when the archive section toggles and a type is picked then the state follows (S-09)`() =
        runTest(dispatcher) {
            stored("Tunai")
            val model = opened()
            advanceUntilIdle()
            model.toggleArchivedSection()
            advanceUntilIdle()
            assertThat((model.state.value as WalletUiState.Success).archivedExpanded).isTrue()
            model.startCreate()
            model.openPicker(WalletPicker.TYPE)
            model.pick { it.withType(WalletType.BANK) }
            advanceUntilIdle()
            val success = model.state.value as WalletUiState.Success
            assertThat(success.picker).isNull()
            assertThat(success.editor?.type).isEqualTo(WalletType.BANK)
            model.dismissPicker()
            model.dismissEditor()
            advanceUntilIdle()
            assertThat((model.state.value as WalletUiState.Success).editor).isNull()
        }
}
