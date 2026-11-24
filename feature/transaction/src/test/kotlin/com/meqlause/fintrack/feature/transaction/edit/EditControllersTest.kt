package com.meqlause.fintrack.feature.transaction.edit

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.loan.LoanDueOption
import com.meqlause.fintrack.core.domain.usecase.SaveLoanUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveQuickEntryUseCase
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeQuickEntryRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import com.meqlause.fintrack.core.testing.time.FakeClock
import com.meqlause.fintrack.feature.transaction.edit.navigation.EditTransactionDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

// FR-039, FR-150, FR-158, BR-047, S-04: the loan and shortcut controllers of the add sheet
@OptIn(ExperimentalCoroutinesApi::class)
class EditControllersTest {
    private val clock = FakeClock(Instant.parse("2026-11-23T03:00:00Z"))
    private val loans = FakeLoanRepository()
    private val transactions = FakeTransactionRepository()
    private val loanController =
        EditLoanController(SaveLoanUseCase(loans, transactions, PassthroughAtomicRunner(), clock), clock)

    /** FR-150: the Pinjaman tab opens a blank loan and closes any drawer. */
    @Test
    fun `given a sheet when the Pinjaman tab is selected then a blank loan opens (FR-150)`() {
        val opened = loanController.reduce(EditTransactionUiState().showing(EditSheet.WALLET), LoanChange.TabSelected)
        assertThat(opened.loan).isEqualTo(LoanForm())
        assertThat(opened.openSheet).isNull()
        val again = loanController.reduce(opened, LoanChange.PersonChanged("Budi"))
        assertThat(loanController.reduce(again, LoanChange.TabSelected).loan?.personName).isEqualTo("Budi")
    }

    /** FR-151: direction and person change the loan; a long name is cut. */
    @Test
    fun `given a loan when direction and person change then the form follows (FR-151)`() {
        val start = loanController.reduce(EditTransactionUiState(), LoanChange.TabSelected)
        val borrowed = loanController.reduce(start, LoanChange.DirectionPicked(LoanDirection.BORROWED))
        assertThat(borrowed.loan?.direction).isEqualTo(LoanDirection.BORROWED)
        val named = loanController.reduce(borrowed, LoanChange.PersonChanged("a".repeat(60)))
        assertThat(named.loan?.personName?.length).isEqualTo(40)
        val blank = loanController.reduce(EditTransactionUiState(), LoanChange.PersonChanged("Sari"))
        assertThat(blank.loan?.personName).isEqualTo("Sari")
    }

    /** FR-158: a due option counts from today and none clears it; the drawer closes. */
    @Test
    fun `given a loan when a due option is picked then the date counts from today (FR-158)`() {
        val start = loanController.reduce(EditTransactionUiState().showing(EditSheet.LOAN_DUE), LoanChange.TabSelected)
        val week = loanController.reduce(start, LoanChange.DuePicked(LoanDueOption.ONE_WEEK))
        assertThat(week.loan?.dueDate).isEqualTo(LocalDate.parse("2026-11-30"))
        assertThat(week.openSheet).isNull()
        assertThat(loanController.reduce(week, LoanChange.DuePicked(LoanDueOption.NONE)).loan?.dueDate).isNull()
    }

    /** FR-150: a complete loan is stored; the rules reject a draft without a person. */
    @Test
    fun `given a loan draft when stored then a person is required (FR-150)`() =
        runTest {
            val blank = loanController.reduce(EditTransactionUiState(), LoanChange.TabSelected)
            assertThat(
                loanController.store(
                    blank,
                ) is com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult.Rejected,
            ).isTrue()
        }

    private fun shortcutController(
        quick: FakeQuickEntryRepository,
        state: MutableStateFlow<EditTransactionUiState>,
        scope: CoroutineScope,
    ) = EditShortcutController(
        quick,
        SaveQuickEntryUseCase(quick, FakeCategoryRepository(), FakeWalletRepository(transactions)),
        state,
        scope,
    )

    private fun shortcut(title: String): QuickEntry =
        QuickEntry(
            quickEntryId = QuickEntryId(0L),
            template =
                QuickTemplate(
                    title = title,
                    type = TransactionType.EXPENSE,
                    amount = Money(25_000L),
                    note = "tanpa gula",
                ),
            categoryId = CategoryId(1L),
            walletId = WalletId(2L),
            sortOrder = 0,
            audit = Audit(Instant.EPOCH, Instant.EPOCH),
        )

    /** FR-039: a shortcut fills the draft; deleting keeps it restorable for five seconds, then forgets it. */
    @Test
    fun `given a shortcut when picked deleted and restored then the sheet follows (FR-039)`() =
        runTest {
            val quick = FakeQuickEntryRepository()
            val savedId = quick.insert(shortcut("Kopi"))
            val listed = listOf(shortcut("Kopi").copy(quickEntryId = savedId))
            val state = MutableStateFlow(EditTransactionUiState().withShortcuts { copy(shortcuts = listed) })
            val controller = shortcutController(quick, state, backgroundScope)
            assertThat(controller.find(savedId.value)).isNotNull()
            controller.pick(QuickEntryId(999L))
            controller.delete(QuickEntryId(999L))
            controller.restore()
            controller.pick(savedId)
            assertThat(state.value.appliedShortcut).isTrue()
            assertThat(state.value.form.note).isEqualTo("tanpa gula")
            controller.delete(savedId)
            runCurrent()
            assertThat(state.value.removedShortcut).isNotNull()
            controller.delete(savedId)
            controller.restore()
            runCurrent()
            assertThat(state.value.removedShortcut).isNull()
            controller.delete(savedId)
            advanceTimeBy(5_001L)
            runCurrent()
            assertThat(state.value.removedShortcut).isNull()
        }

    /** BR-047: a draft not marked as a shortcut stores nothing; a marked one with a blank title is rejected. */
    @Test
    fun `given the keep checkbox when it is off or the title is blank then the shortcut rules apply (BR-047)`() =
        runTest {
            val quick = FakeQuickEntryRepository()
            val state = MutableStateFlow(EditTransactionUiState())
            val controller = shortcutController(quick, state, backgroundScope)
            assertThat(controller.storeIfKept(state.value, clock.instant())).isTrue()
            val form =
                TransactionForm(rupiahDigits = "25000").withRoute {
                    copy(walletId = WalletId(1L), categoryId = CategoryId(1L))
                }
            state.value = EditTransactionUiState(form = form).withShortcuts { copy(keepAsShortcut = true) }
            assertThat(controller.storeIfKept(state.value, clock.instant())).isFalse()
            assertThat(state.value.shortcutErrors.isNotEmpty()).isTrue()
        }

    /** FR-039: the checkbox and the typed Nama go through the shortcut controller; other events are not its own. */
    @Test
    fun `given the shortcut events when handled then the name and checkbox are kept (FR-039)`() =
        runTest {
            val quick = FakeQuickEntryRepository()
            val state = MutableStateFlow(EditTransactionUiState())
            val controller = shortcutController(quick, state, backgroundScope)

            assertThat(controller.handle(EditTransactionEvent.ShortcutToggled(true))).isTrue()
            assertThat(controller.handle(EditTransactionEvent.ShortcutNameChanged("Kopi pagi"))).isTrue()
            assertThat(controller.handle(EditTransactionEvent.NoteChanged("x"))).isFalse()

            assertThat(state.value.keepAsShortcut).isTrue()
            assertThat(state.value.shortcutName).isEqualTo("Kopi pagi")
        }

    /** FR-140: destinations compare by their ids. */
    @Test
    fun `given edit destinations when compared then they follow their ids (FR-034)`() {
        val base = EditTransactionDestination(transactionId = 1L, quickEntryId = 2L)
        assertThat(base).isEqualTo(EditTransactionDestination(1L, 2L))
        assertThat(base.hashCode()).isEqualTo(EditTransactionDestination(1L, 2L).hashCode())
        assertThat(base).isNotEqualTo(EditTransactionDestination(1L, 3L))
        assertThat(base).isNotEqualTo(EditTransactionDestination(9L, 2L))
        assertThat(EditTransactionDestination()).isNotEqualTo(base)
        assertThat(base.copy(transactionId = null).transactionId).isNull()
        assertThat(base.toString()).isEqualTo("EditTransactionDestination(transactionId=1, quickEntryId=2)")
    }
}
