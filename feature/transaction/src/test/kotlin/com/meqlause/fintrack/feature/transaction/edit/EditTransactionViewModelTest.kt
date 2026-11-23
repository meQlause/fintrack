package com.meqlause.fintrack.feature.transaction.edit

import assertk.assertThat
import assertk.assertions.containsExactly

import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.domain.usecase.SaveTransactionUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.coroutine.MainDispatcherRule
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.Instant

// FR-030..FR-033, BR-001..BR-003, BR-039: the sheet's validation messages and the save path, driven through fakes
class EditTransactionViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val clock = FakeClock(Instant.parse("2026-11-23T07:05:00Z"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val categories = FakeCategoryRepository()
    private val settings = FakeSettingsRepository()

    private fun newViewModel(): EditTransactionViewModel =
        EditTransactionViewModel(
            transactionRepository = transactions,
            walletRepository = wallets,
            categoryRepository = categories,
            settingsRepository = settings,
            saveTransaction = SaveTransactionUseCase(transactions, wallets, categories, clock),
            clock = clock,
        )

    /** FR-030, BR-002: an amount without a category shows "Pilih kategori" and stores nothing. */
    @Test
    fun givenAmountButNoCategoryWhenSavingThenCategoryRequiredIsShown() =
        runTest {
            seedWallet(WalletType.BANK)
            val viewModel = newViewModel()
            viewModel.open(null)
            advanceUntilIdle()
            viewModel.onEvent(EditTransactionEvent.DigitTyped("5"))
            viewModel.onEvent(EditTransactionEvent.SaveRequested)
            advanceUntilIdle()
            assertThat(viewModel.state.value.errors).isEqualTo(setOf(TransactionError.CATEGORY_REQUIRED))
        }

    /** BR-001: a zero amount shows "Masukkan jumlah". */
    @Test
    fun givenZeroAmountWhenSavingThenAmountRequiredIsShown() =
        runTest {
            seedWallet(WalletType.BANK)
            val viewModel = newViewModel()
            viewModel.open(null)
            advanceUntilIdle()
            viewModel.onEvent(EditTransactionEvent.SaveRequested)
            advanceUntilIdle()
            assertThat(viewModel.state.value.errors).contains(TransactionError.AMOUNT_REQUIRED)
        }

    /** BR-003: a transfer whose target is the source shows "Dompet asal dan tujuan harus berbeda". */
    @Test
    fun givenTransferToSameWalletWhenSavingThenSameWalletIsShown() =
        runTest {
            val bank = seedWallet(WalletType.BANK)
            val viewModel = newViewModel()
            viewModel.open(null)
            advanceUntilIdle()
            viewModel.onEvent(EditTransactionEvent.TypeSelected(TransactionType.TRANSFER))
            viewModel.onEvent(EditTransactionEvent.SheetOpened(EditSheet.TARGET_WALLET))
            viewModel.onEvent(EditTransactionEvent.WalletPicked(bank))
            viewModel.onEvent(EditTransactionEvent.DigitTyped("5"))
            viewModel.onEvent(EditTransactionEvent.SaveRequested)
            advanceUntilIdle()
            assertThat(viewModel.state.value.errors).isEqualTo(setOf(TransactionError.SAME_WALLET))
        }

    /** BR-039: a credit wallet preselected as the source of a transfer is rejected. */
    @Test
    fun givenCreditWalletAsTransferSourceWhenSavingThenItIsRejected() =
        runTest {
            seedWallet(WalletType.CREDIT_CARD)
            val bank = seedWallet(WalletType.BANK)
            val viewModel = newViewModel()
            viewModel.open(null)
            advanceUntilIdle()
            viewModel.onEvent(EditTransactionEvent.TypeSelected(TransactionType.TRANSFER))
            viewModel.onEvent(EditTransactionEvent.SheetOpened(EditSheet.TARGET_WALLET))
            viewModel.onEvent(EditTransactionEvent.WalletPicked(bank))
            viewModel.onEvent(EditTransactionEvent.DigitTyped("5"))
            viewModel.onEvent(EditTransactionEvent.SaveRequested)
            advanceUntilIdle()
            assertThat(viewModel.state.value.errors).isEqualTo(setOf(TransactionError.CREDIT_SOURCE_NOT_ALLOWED))
        }

    /** FR-030, FR-033: amount and category are enough; the expense is stored and the sheet closes. */
    @Test
    fun givenAmountAndCategoryWhenSavingThenExpenseIsStoredAndSheetCloses() =
        runTest {
            seedWallet(WalletType.BANK)
            val food = seedCategory(CategoryKind.EXPENSE)
            val viewModel = newViewModel()
            viewModel.open(null)
            advanceUntilIdle()
            viewModel.onEvent(EditTransactionEvent.DigitTyped("2"))
            viewModel.onEvent(EditTransactionEvent.DigitTyped("5"))
            viewModel.onEvent(EditTransactionEvent.CategoryPicked(food))
            viewModel.onEvent(EditTransactionEvent.SaveRequested)
            advanceUntilIdle()
            assertThat(transactions.observeAll().first().map { it.movement.amount }).containsExactly(Money(25L))
            assertThat(viewModel.state.value.isSaved).isTrue()
        }

    private suspend fun seedWallet(type: WalletType): WalletId =
        wallets.insert(
            Wallet(
                walletId = WalletId(0L),
                kind = WalletKind(type, null),
                terms = WalletTerms(Money(0L), null),
                profile = WalletProfile("Dompet", Appearance("wallet", "teal"), 0),
                status = WalletStatus(),
                audit = Audit(clock.instant(), clock.instant()),
            ),
        )

    private suspend fun seedCategory(kind: CategoryKind): CategoryId =
        categories.insert(
            Category(
                categoryId = CategoryId(0L),
                profile = CategoryProfile("Makan", kind, Appearance("tag", "teal"), 0),
                state = CategoryState(isDefault = true, isArchived = false),
                audit = Audit(clock.instant(), clock.instant()),
            ),
        )
}
