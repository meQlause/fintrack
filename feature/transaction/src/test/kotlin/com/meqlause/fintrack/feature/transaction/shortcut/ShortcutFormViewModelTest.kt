package com.meqlause.fintrack.feature.transaction.shortcut

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryError
import com.meqlause.fintrack.core.domain.usecase.SaveQuickEntryUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.CreditTerms
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
import com.meqlause.fintrack.core.testing.repository.FakeQuickEntryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import com.meqlause.fintrack.feature.transaction.TransactionSources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.Instant

// FR-039, BR-006, BR-047: the shortcut form saves a shortcut with its own name and note and never a transaction
@OptIn(ExperimentalCoroutinesApi::class)
class ShortcutFormViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val clock = FakeClock(Instant.parse("2026-11-24T07:05:00Z"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val categories = FakeCategoryRepository()
    private val quickEntries = FakeQuickEntryRepository()

    /** FR-039, BR-047: a filled form stores the name, amount, category, wallet and note, and no transaction. */
    @Test
    fun givenFilledFormWhenSavingThenOnlyTheShortcutIsStored() =
        runTest {
            val wallet = seedWallet(WalletType.BANK)
            val category = seedCategory(CategoryKind.EXPENSE)
            val viewModel = newViewModel()
            viewModel.open(null)
            advanceUntilIdle()

            viewModel.fill(name = "Kopi pagi", digits = "25000", category = category, note = "tanpa gula")
            viewModel.onEvent(ShortcutFormEvent.SaveRequested)
            advanceUntilIdle()

            val stored = quickEntries.observeAll().first().single()
            assertThat(stored.template.title).isEqualTo("Kopi pagi")
            assertThat(stored.template.note).isEqualTo("tanpa gula")
            assertThat(stored.template.amount).isEqualTo(Money(25_000L))
            assertThat(stored.walletId).isEqualTo(wallet)
            assertThat(transactions.observeAll().first()).isEmpty()
            assertThat(viewModel.state.value.status.isSaved).isTrue()
        }

    /** FR-039: a new form starts on the first wallet and cannot be saved without a name, amount and category. */
    @Test
    fun givenNewFormWhenNothingIsFilledThenWalletIsDefaultedAndSaveIsDisabled() =
        runTest {
            val wallet = seedWallet(WalletType.CASH)
            val viewModel = newViewModel()
            viewModel.open(null)
            advanceUntilIdle()

            assertThat(viewModel.state.value.draft.walletId).isEqualTo(wallet)
            assertThat(viewModel.state.value.canSave).isFalse()
        }

    /** FR-039: opening a stored shortcut edits it in place, keeping a single row. */
    @Test
    fun givenStoredShortcutWhenEditedThenItIsUpdatedInPlace() =
        runTest {
            seedWallet(WalletType.BANK)
            val category = seedCategory(CategoryKind.EXPENSE)
            val first = newViewModel()
            first.open(null)
            advanceUntilIdle()
            first.fill(name = "Kopi", digits = "20000", category = category, note = "")
            first.onEvent(ShortcutFormEvent.SaveRequested)
            advanceUntilIdle()
            val storedId =
                quickEntries
                    .observeAll()
                    .first()
                    .single()
                    .quickEntryId.value

            val editor = newViewModel()
            editor.open(storedId)
            advanceUntilIdle()
            assertThat(editor.state.value.isEditing).isTrue()
            editor.onEvent(ShortcutFormEvent.NameChanged("Kopi susu"))
            editor.onEvent(ShortcutFormEvent.NoteChanged("oat"))
            editor.onEvent(ShortcutFormEvent.SaveRequested)
            advanceUntilIdle()

            val stored = quickEntries.observeAll().first().single()
            assertThat(stored.template.title).isEqualTo("Kopi susu")
            assertThat(stored.template.note).isEqualTo("oat")
        }

    /** BR-006, BR-039: switching to income drops the expense category and a credit wallet. */
    @Test
    fun givenExpenseDraftWhenSwitchedToIncomeThenCategoryAndCreditWalletAreDropped() =
        runTest {
            val card = seedCreditCard()
            seedWallet(WalletType.CASH)
            val category = seedCategory(CategoryKind.EXPENSE)
            val viewModel = newViewModel()
            viewModel.open(null)
            advanceUntilIdle()
            viewModel.onEvent(ShortcutFormEvent.WalletPicked(card))
            viewModel.onEvent(ShortcutFormEvent.CategoryPicked(category))

            viewModel.onEvent(ShortcutFormEvent.TypeSelected(TransactionType.INCOME))
            advanceUntilIdle()

            assertThat(viewModel.state.value.draft.categoryId).isNull()
            assertThat(viewModel.state.value.draft.walletId).isNull()
            assertThat(viewModel.state.value.categoryChoices).isEmpty()
        }

    /** FR-039: the name stops at 24 characters, the amount keeps digits only and the drawers open and close. */
    @Test
    fun givenTypingWhenEditingThenInputIsLimitedAndDrawersToggle() =
        runTest {
            val viewModel = newViewModel()
            viewModel.open(null)
            viewModel.onEvent(ShortcutFormEvent.NameChanged("x".repeat(30)))
            viewModel.onEvent(ShortcutFormEvent.AmountChanged("0Rp 1.500"))
            viewModel.onEvent(ShortcutFormEvent.SheetOpened(ShortcutSheet.CATEGORY))
            assertThat(viewModel.state.value.openSheet).isEqualTo(ShortcutSheet.CATEGORY)
            viewModel.onEvent(ShortcutFormEvent.SheetDismissed)

            assertThat(viewModel.state.value.draft.name.length).isEqualTo(24)
            assertThat(viewModel.state.value.draft.rupiahDigits).isEqualTo("1500")
            assertThat(viewModel.state.value.openSheet).isNull()
        }

    /** FR-039: a rule the use case finds, here a wallet that is not allowed, keeps the form open with the error. */
    @Test
    fun givenIncomeOnCreditWalletWhenSavingThenTheRuleIsShownAndNothingIsStored() =
        runTest {
            val card = seedCreditCard()
            val salary = seedCategory(CategoryKind.INCOME)
            val viewModel = newViewModel()
            viewModel.open(null)
            advanceUntilIdle()
            viewModel.onEvent(ShortcutFormEvent.TypeSelected(TransactionType.INCOME))
            viewModel.fill(name = "Gaji", digits = "100", category = salary, note = "")
            viewModel.onEvent(ShortcutFormEvent.WalletPicked(card))
            viewModel.onEvent(ShortcutFormEvent.SaveRequested)
            advanceUntilIdle()

            assertThat(viewModel.state.value.status.errors).isEqualTo(setOf(QuickEntryError.WALLET_NOT_ALLOWED))
            assertThat(quickEntries.observeAll().first()).isEmpty()
        }

    private fun ShortcutFormViewModel.fill(
        name: String,
        digits: String,
        category: CategoryId,
        note: String,
    ) {
        onEvent(ShortcutFormEvent.NameChanged(name))
        onEvent(ShortcutFormEvent.AmountChanged(digits))
        onEvent(ShortcutFormEvent.CategoryPicked(category))
        onEvent(ShortcutFormEvent.NoteChanged(note))
    }

    private fun newViewModel(): ShortcutFormViewModel =
        ShortcutFormViewModel(
            sources = TransactionSources(transactions, wallets, categories, FakeSettingsRepository()),
            quickEntries = quickEntries,
            saveQuickEntry = SaveQuickEntryUseCase(quickEntries, categories, wallets),
            clock = clock,
        )

    private suspend fun seedWallet(type: WalletType): WalletId = wallets.insert(wallet(type, "Dompet", credit = null))

    private suspend fun seedCreditCard(): WalletId =
        wallets.insert(
            wallet(WalletType.CREDIT_CARD, "Kartu", CreditTerms(Money(10_000_000L), statementDay = 25, dueDay = 10)),
        )

    private fun wallet(
        type: WalletType,
        name: String,
        credit: CreditTerms?,
    ): Wallet =
        Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type, null),
            terms = WalletTerms(Money(0L), credit),
            profile = WalletProfile(name, Appearance("wallet", "teal"), 0),
            status = WalletStatus(),
            audit = Audit(clock.instant(), clock.instant()),
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
