package com.meqlause.fintrack.feature.transaction.edit

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
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
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.time.FakeClock
import org.junit.Test
import java.time.Instant

// FR-030..FR-034, FR-140, S-04: the pure rules of the add and edit sheet state
class EditStateLogicTest {
    private val stamp = Instant.parse("2026-11-23T03:00:00Z")
    private val audit = Audit(Instant.EPOCH, Instant.EPOCH)

    private fun wallet(
        walletKey: Long,
        type: WalletType,
        archived: Boolean = false,
    ): Wallet =
        Wallet(
            WalletId(walletKey),
            WalletKind(type, investKind = null),
            WalletTerms(Money(0L), credit = null),
            WalletProfile("Dompet $walletKey", Appearance("bank", "blue"), sortOrder = walletKey.toInt()),
            WalletStatus(isArchived = archived),
            audit,
        )

    private fun category(
        catKey: Long,
        kind: CategoryKind,
        archived: Boolean = false,
    ): Category =
        Category(
            CategoryId(catKey),
            CategoryProfile("Kategori $catKey", kind, Appearance("food", "orange"), sortOrder = -catKey.toInt()),
            CategoryState(isDefault = false, isArchived = archived),
            audit,
        )

    private val catalog =
        EditCatalog(
            wallets =
                WalletType.entries.mapIndexed { index, type -> wallet(index + 1L, type) } +
                    wallet(20L, WalletType.CASH, true),
            moneyByWallet = mapOf(WalletId(1L) to Money(1_500_000L)),
            categories =
                listOf(
                    category(1L, CategoryKind.EXPENSE),
                    category(2L, CategoryKind.EXPENSE),
                    category(3L, CategoryKind.INCOME),
                    category(4L, CategoryKind.EXPENSE, true),
                ),
        )

    /** S-04: the wallet row names the wallet, its compact funds and its group; an unknown wallet is blank. */
    @Test
    fun `given a catalog when wallet rows are read then name funds and group follow the wallet (S-04)`() {
        val state = EditTransactionUiState(catalog = catalog)
        val groups = WalletType.entries.indices.map { EditWalletInfo.forWallet(state, WalletId(it + 1L)).groupRes }
        assertThat(groups.filterNotNull().size).isEqualTo(WalletType.entries.size)
        val first = EditWalletInfo.forWallet(state, WalletId(1L))
        assertThat(first.name).isEqualTo("Dompet 1")
        assertThat(first.fundsText.isNotEmpty()).isTrue()
        val unknown = EditWalletInfo.forWallet(state, null)
        assertThat(unknown.name).isEqualTo("")
        assertThat(unknown.fundsText).isEqualTo("")
        assertThat(unknown.groupRes).isNull()
    }

    /** BR-007: archived wallets and categories are not offered; categories come in their own order. */
    @Test
    fun `given a catalog when listed then archived rows are left out and kinds are split (BR-007)`() {
        assertThat(catalog.activeWallets().size).isEqualTo(WalletType.entries.size)
        assertThat(catalog.categoriesOf(CategoryKind.EXPENSE).map { it.categoryId.value }).isEqualTo(listOf(2L, 1L))
        assertThat(catalog.categoriesOf(CategoryKind.INCOME).size).isEqualTo(1)
        assertThat(catalog.isCreditWallet(WalletId(4L))).isTrue()
        assertThat(catalog.isCreditWallet(WalletId(1L))).isFalse()
        assertThat(catalog.isCreditWallet(null)).isFalse()
    }

    /** FR-140: the tenor is valid only from 2 to 36 and the fee reads digits or zero. */
    @Test
    fun `given an instalment form when the tenor and fee are typed then the ranges apply (FR-140)`() {
        assertThat(InstalmentForm().tenor).isEqualTo(6)
        assertThat(InstalmentForm(tenorDigits = "1").tenor).isNull()
        assertThat(InstalmentForm(tenorDigits = "37").tenor).isNull()
        assertThat(InstalmentForm(tenorDigits = "").tenor).isNull()
        assertThat(InstalmentForm(feeDigits = "5000").feeAmount).isEqualTo(Money(5_000L))
        assertThat(InstalmentForm().feeAmount).isEqualTo(Money(0L))
    }

    /** FR-030, FR-140: the keypad edits the focused field, whichever it is, and clears the errors. */
    @Test
    fun `given each focused field when the keypad edits then only that field changes (FR-030)`() {
        val base = EditTransactionUiState()

        fun edit(field: KeypadField) = base.withStatus { copy(keypadField = field) }.withEditedField { it + "7" }
        assertThat(edit(KeypadField.AMOUNT).form.rupiahDigits).isEqualTo("7")
        assertThat(edit(KeypadField.FEE).form.feeDigits).isEqualTo("7")
        assertThat(edit(KeypadField.TENOR).instalment.tenorDigits).isEqualTo("67")
        assertThat(edit(KeypadField.INSTALMENT_FEE).instalment.feeDigits).isEqualTo("7")
    }

    /** S-04: the sheet can save only with an amount, a wallet and its route; the loan needs a person. */
    @Test
    fun `given a sheet when its parts are filled then saving follows the rules (S-04)`() {
        val empty = EditTransactionUiState(catalog = catalog)
        assertThat(empty.canSave).isFalse()
        val route = TransactionForm(rupiahDigits = "1000").withRoute { copy(walletId = WalletId(1L)) }
        val noCategory = empty.copy(form = route)
        assertThat(noCategory.canSave).isFalse()
        val expense = noCategory.copy(form = route.withRoute { copy(categoryId = CategoryId(1L)) })
        assertThat(expense.canSave).isTrue()
        assertThat(expense.categoryChoices.size).isEqualTo(2)
        val transfer = TransactionForm(TransactionType.TRANSFER, "1000").withRoute { copy(walletId = WalletId(1L)) }
        assertThat(empty.copy(form = transfer).canSave).isFalse()
        assertThat(
            empty.copy(form = transfer.withRoute { copy(targetWalletId = WalletId(2L)) }).categoryChoices,
        ).isEmpty()
        val loan = noCategory.withExtras { copy(loan = LoanForm()) }
        assertThat(loan.canSave).isFalse()
        assertThat(loan.withExtras { copy(loan = LoanForm(personName = "Budi")) }.canSave).isTrue()
    }

    /** FR-140: instalments are offered for a new expense on a credit wallet, and need a tenor when enabled. */
    @Test
    fun `given a credit wallet when an expense is drafted then the instalment block follows (FR-140)`() {
        val form =
            TransactionForm(rupiahDigits = "1000").withRoute {
                copy(walletId = WalletId(4L), categoryId = CategoryId(1L))
            }
        val state = EditTransactionUiState(form = form, catalog = catalog)
        assertThat(state.offersInstalment).isTrue()
        assertThat(state.instalmentActive).isFalse()
        val enabled = state.withExtras { copy(instalment = InstalmentForm(enabled = true, tenorDigits = "1")) }
        assertThat(enabled.instalmentActive).isTrue()
        assertThat(enabled.canSave).isFalse()
        assertThat(state.copy(form = form.copy(type = TransactionType.INCOME)).offersInstalment).isFalse()
        assertThat(state.copy(form = form.withRoute { copy(walletId = WalletId(1L)) }).offersInstalment).isFalse()
    }

    /** FR-039, BR-047: the shortcut checkbox and chip are offered only on a plain new draft. */
    @Test
    fun `given drafts when shortcuts are offered then only plain new drafts qualify (BR-047)`() {
        val plain = EditTransactionUiState(catalog = catalog)
        assertThat(plain.canKeepShortcut).isTrue()
        assertThat(plain.offersShortcutChip).isTrue()
        assertThat(plain.withShortcuts { copy(appliedShortcut = true) }.canKeepShortcut).isFalse()
        assertThat(plain.copy(form = TransactionForm(TransactionType.TRANSFER)).offersShortcutChip).isFalse()
        assertThat(plain.withExtras { copy(loan = LoanForm()) }.canKeepShortcut).isFalse()
        val existing = recordFor(transaction(), FakeClock(stamp))
        assertThat(plain.copy(record = existing).isEditing).isTrue()
        assertThat(plain.copy(record = existing).offersShortcutChip).isFalse()
    }

    private fun transaction(): Transaction =
        Transaction(
            TransactionId(1L),
            Movement(TransactionType.TRANSFER, Money(100_000L), Money(2_500L)),
            Routing(WalletId(1L), WalletId(2L), categoryId = null, loanId = null),
            Memo(note = " Catatan "),
            Origin(TransactionSource.MANUAL, inboxItemId = null),
            Timing(stamp, audit),
        )

    /** FR-034: a stored transaction fills the form, and the form maps back to a draft. */
    @Test
    fun `given a stored transfer when edited then the form and the draft carry its parts (FR-034)`() {
        val form = transaction().toForm()
        assertThat(form.rupiahDigits).isEqualTo("100000")
        assertThat(form.feeDigits).isEqualTo("2500")
        assertThat(form.targetWalletId).isEqualTo(WalletId(2L))
        val draft = EditTransactionUiState(form = form, record = recordFor(transaction(), FakeClock(stamp))).toDraft()
        assertThat(draft.movement.feeAmount).isEqualTo(Money(2_500L))
        assertThat(draft.routing.categoryId).isNull()
        assertThat(draft.memo.note).isEqualTo("Catatan")
        assertThat(draft.occurredAt).isEqualTo(stamp)
        val expense =
            EditTransactionUiState(
                form =
                    TransactionForm(rupiahDigits = "5").withRoute {
                        copy(categoryId = CategoryId(1L))
                    },
            ).toDraft()
        assertThat(expense.movement.feeAmount).isEqualTo(Money(0L))
        assertThat(expense.routing.categoryId).isEqualTo(CategoryId(1L))
        assertThat(TransactionType.TRANSFER.categoryKind()).isNull()
        assertThat(TransactionType.INCOME.categoryKind()).isEqualTo(CategoryKind.INCOME)
    }

    /** S-04: changing the type keeps a fitting category and drops the target wallet outside a transfer. */
    @Test
    fun `given a draft when its type changes then the category and target follow (S-04)`() {
        val form =
            TransactionForm(rupiahDigits = "1").withRoute {
                copy(walletId = WalletId(1L), targetWalletId = WalletId(2L), categoryId = CategoryId(3L))
            }
        val state = EditTransactionUiState(form = form, catalog = catalog)
        val income = state.withType(TransactionType.INCOME)
        assertThat(income.form.categoryId).isEqualTo(CategoryId(3L))
        assertThat(income.form.targetWalletId).isNull()
        assertThat(state.withType(TransactionType.EXPENSE).form.categoryId).isNull()
        assertThat(state.withType(TransactionType.TRANSFER).form.targetWalletId).isEqualTo(WalletId(2L))
        val picked = state.showing(EditSheet.TARGET_WALLET).withPickedWallet(WalletId(5L))
        assertThat(picked.form.targetWalletId).isEqualTo(WalletId(5L))
        assertThat(picked.openSheet).isNull()
        assertThat(
            state
                .showing(EditSheet.WALLET)
                .withPickedWallet(WalletId(5L))
                .form.walletId,
        ).isEqualTo(WalletId(5L))
        assertThat(state.withSwappedWallets().form.walletId).isEqualTo(WalletId(2L))
        assertThat(
            state
                .withNote("x".repeat(200))
                .form.note.length,
        ).isEqualTo(120)
        assertThat(state.withCategory(null).form.categoryId).isNull()
    }
}
