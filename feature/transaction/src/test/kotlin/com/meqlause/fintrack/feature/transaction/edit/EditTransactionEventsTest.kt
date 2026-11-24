package com.meqlause.fintrack.feature.transaction.edit

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.loan.LoanDueOption
import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.domain.usecase.CreateCategoryUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveInstalmentPurchaseUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveLoanUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveQuickEntryUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveTransactionUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
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
import com.meqlause.fintrack.core.testing.repository.FakeInstallmentPlanRepository
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeQuickEntryRepository
import com.meqlause.fintrack.core.testing.repository.FakeSettingsRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import com.meqlause.fintrack.core.testing.time.FakeClock
import com.meqlause.fintrack.feature.transaction.TransactionSources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.Instant

// FR-030..FR-039, FR-140, FR-150, S-04: every event of the add sheet through EditTransactionViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class EditTransactionEventsTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val clock = FakeClock(Instant.parse("2026-11-23T07:05:00Z"))
    private val transactions = FakeTransactionRepository()
    private val wallets = FakeWalletRepository(transactions)
    private val categories = FakeCategoryRepository()
    private val quick = FakeQuickEntryRepository()
    private val saveTransaction = SaveTransactionUseCase(transactions, wallets, categories, clock)

    private fun viewModel(): EditTransactionViewModel =
        EditTransactionViewModel(
            sources = TransactionSources(transactions, wallets, categories, FakeSettingsRepository()),
            instalments =
                EditInstalmentController(
                    saveTransaction,
                    SaveInstalmentPurchaseUseCase(
                        saveTransaction,
                        wallets,
                        categories,
                        FakeInstallmentPlanRepository(),
                        PassthroughAtomicRunner(),
                        clock,
                    ),
                ),
            loans =
                EditLoanController(
                    SaveLoanUseCase(FakeLoanRepository(), transactions, PassthroughAtomicRunner(), clock),
                    clock,
                ),
            shortcutDeps = EditShortcutDeps(quick, SaveQuickEntryUseCase(quick)),
            createCategory = CreateCategoryUseCase(categories, clock),
            clock = clock,
        )

    private suspend fun wallet(type: WalletType): WalletId =
        wallets.insert(
            Wallet(
                WalletId(0L),
                WalletKind(type, investKind = null),
                WalletTerms(Money(5_000_000L), credit = null),
                WalletProfile("Dompet", Appearance("bank", "blue"), sortOrder = 0),
                WalletStatus(),
                Audit(Instant.EPOCH, Instant.EPOCH),
            ),
        )

    private suspend fun expenseCategory(): CategoryId =
        categories.insert(
            Category(
                CategoryId(0L),
                CategoryProfile("Makan", CategoryKind.EXPENSE, Appearance("food", "orange"), sortOrder = 0),
                CategoryState(isDefault = false, isArchived = false),
                Audit(Instant.EPOCH, Instant.EPOCH),
            ),
        )

    private fun TestScope.opened(): EditTransactionViewModel {
        val model = viewModel()
        model.open(null)
        advanceUntilIdle()
        return model
    }

    private fun EditTransactionViewModel.send(vararg events: EditTransactionEvent) = events.forEach(::onEvent)

    /** FR-030: digits, backspace, the focused field and the type tabs shape the draft. */
    @Test
    fun `given the sheet when keys fields and types change then the draft follows (FR-030)`() =
        runTest {
            wallet(WalletType.BANK)
            val model = opened()
            model.send(
                EditTransactionEvent.DigitTyped("0"),
                EditTransactionEvent.DigitTyped("5"),
                EditTransactionEvent.DigitTyped("2"),
            )
            model.send(EditTransactionEvent.BackspacePressed)
            assertThat(model.state.value.form.rupiahDigits).isEqualTo("5")
            model.send(EditTransactionEvent.FieldFocused(KeypadField.FEE), EditTransactionEvent.DigitTyped("9"))
            assertThat(model.state.value.form.feeDigits).isEqualTo("9")
            model.send(EditTransactionEvent.TypeSelected(TransactionType.TRANSFER), EditTransactionEvent.WalletsSwapped)
            assertThat(model.state.value.form.isTransfer).isTrue()
            model.send(EditTransactionEvent.NoteChanged("catatan"))
            assertThat(model.state.value.form.note).isEqualTo("catatan")
        }

    /** FR-032: drawers open and close, and a picked wallet lands on the side that opened the drawer. */
    @Test
    fun `given the drawers when wallets are picked then the side that opened them gets the wallet (FR-032)`() =
        runTest {
            val bank = wallet(WalletType.BANK)
            val cash = wallet(WalletType.CASH)
            val model = opened()
            model.send(EditTransactionEvent.TypeSelected(TransactionType.TRANSFER))
            model.send(EditTransactionEvent.SheetOpened(EditSheet.WALLET), EditTransactionEvent.WalletPicked(bank))
            assertThat(model.state.value.openSheet).isNull()
            model.send(
                EditTransactionEvent.SheetOpened(EditSheet.TARGET_WALLET),
                EditTransactionEvent.WalletPicked(cash),
            )
            assertThat(model.state.value.form.targetWalletId).isEqualTo(cash)
            model.send(EditTransactionEvent.SheetOpened(EditSheet.SHORTCUTS), EditTransactionEvent.SheetDismissed)
            assertThat(model.state.value.openSheet).isNull()
        }

    /** BR-002: saving without a category is rejected with its message; a full expense is stored. */
    @Test
    fun `given an expense when saved incomplete then rejected and complete then stored (BR-002)`() =
        runTest {
            val bank = wallet(WalletType.BANK)
            val food = expenseCategory()
            val model = opened()
            model.send(
                EditTransactionEvent.WalletPicked(bank),
                EditTransactionEvent.DigitTyped("5"),
                EditTransactionEvent.SaveRequested,
            )
            advanceUntilIdle()
            assertThat(
                model.state.value.errors
                    .contains(TransactionError.CATEGORY_REQUIRED),
            ).isTrue()
            assertThat(model.state.value.isSaved).isFalse()
            model.send(EditTransactionEvent.CategoryPicked(food), EditTransactionEvent.SaveRequested)
            advanceUntilIdle()
            assertThat(model.state.value.isSaved).isTrue()
            assertThat(transactions.observeAll().first().size).isEqualTo(1)
        }

    /** FR-150: the Pinjaman tab stores a loan with a person and a due date. */
    @Test
    fun `given the Pinjaman tab when a person and a due date are given then the loan is stored (FR-150)`() =
        runTest {
            val bank = wallet(WalletType.BANK)
            val model = opened()
            model.send(EditTransactionEvent.WalletPicked(bank), EditTransactionEvent.DigitTyped("7"))
            model.send(
                EditTransactionEvent.LoanEdited(LoanChange.TabSelected),
                EditTransactionEvent.LoanEdited(LoanChange.PersonChanged("Budi")),
            )
            model.send(
                EditTransactionEvent.LoanEdited(LoanChange.DuePicked(LoanDueOption.ONE_MONTH)),
                EditTransactionEvent.SaveRequested,
            )
            advanceUntilIdle()
            assertThat(model.state.value.isSaved).isTrue()
        }

    /** FR-039: a shortcut can be kept, picked, deleted and restored from the sheet. */
    @Test
    fun `given a stored shortcut when picked deleted and restored then the sheet follows (FR-039)`() =
        runTest {
            val bank = wallet(WalletType.BANK)
            val food = expenseCategory()
            val savedId =
                quick.insert(
                    QuickEntry(
                        QuickEntryId(0L),
                        QuickTemplate("Kopi", TransactionType.EXPENSE, Money(25_000L)),
                        food,
                        bank,
                        0,
                        Audit(Instant.EPOCH, Instant.EPOCH),
                    ),
                )
            val model = viewModel()
            model.open(null, quickEntryId = savedId.value)
            advanceUntilIdle()
            assertThat(model.state.value.appliedShortcut).isTrue()
            model.send(EditTransactionEvent.ShortcutPicked(savedId), EditTransactionEvent.ShortcutToggled(true))
            model.send(EditTransactionEvent.ShortcutDeleted(savedId), EditTransactionEvent.ShortcutRestored)
            advanceUntilIdle()
            assertThat(quick.observeAll().first().size).isEqualTo(1)
            assertThat(model.state.value.shortcutErrors).isEmpty()
        }

    /** FR-140: the instalment switch and its tenor choices reach the draft. */
    @Test
    fun `given the instalment block when the tenor is picked then the draft follows (FR-140)`() =
        runTest {
            val card = wallet(WalletType.CREDIT_CARD)
            val model = opened()
            model.send(EditTransactionEvent.WalletPicked(card), EditTransactionEvent.InstalmentToggled(true))
            model.send(EditTransactionEvent.InstalmentTenorPicked(12))
            assertThat(model.state.value.instalment.tenor).isEqualTo(12)
            model.send(EditTransactionEvent.InstalmentTenorPicked(null))
            assertThat(model.state.value.keypadField).isEqualTo(KeypadField.TENOR)
            assertThat(model.state.value.instalment.enabled).isTrue()
        }
}
