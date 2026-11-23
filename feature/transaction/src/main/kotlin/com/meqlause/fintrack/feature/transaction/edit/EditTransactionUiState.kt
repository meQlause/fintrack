package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.quickentry.QuickEntryError
import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.TransactionType
import java.time.Instant

// S-04: everything the add or edit sheet shows; the ViewModel owns it and the screen only reads it
internal data class EditTransactionUiState(
    val form: TransactionForm = TransactionForm(),
    val catalog: EditCatalog = EditCatalog(),
    val record: EditRecord = EditRecord(existing = null, occurredAt = Instant.EPOCH, dateLabel = ""),
    val status: SheetStatus = SheetStatus(),
    val shortcutState: ShortcutState = ShortcutState(),
    val extras: ExtraForms = ExtraForms(),
) {
    val keypadField: KeypadField get() = status.keypadField
    val openSheet: EditSheet? get() = status.openSheet
    val errors: Set<TransactionError> get() = status.errors
    val isSaved: Boolean get() = status.isSaved
    val shortcuts: List<QuickEntry> get() = shortcutState.shortcuts
    val removedShortcut: QuickEntry? get() = shortcutState.removedShortcut
    val keepAsShortcut: Boolean get() = shortcutState.keepAsShortcut
    val appliedShortcut: Boolean get() = shortcutState.appliedShortcut
    val shortcutErrors: Set<QuickEntryError> get() = shortcutState.shortcutErrors
    val instalment: InstalmentForm get() = extras.instalment
    val loan: LoanForm? get() = extras.loan

    // S-04: one change of the keypad, drawer, errors or saved flag
    fun withStatus(change: SheetStatus.() -> SheetStatus): EditTransactionUiState = copy(status = status.change())

    // FR-039: one change of the shortcut list, the undo, the checkbox or the shortcut errors
    fun withShortcuts(change: ShortcutState.() -> ShortcutState): EditTransactionUiState =
        copy(shortcutState = shortcutState.change())

    // FR-140, S-04: one change of the instalment block or the Pinjaman block
    fun withExtras(change: ExtraForms.() -> ExtraForms): EditTransactionUiState = copy(extras = extras.change())

    val isEditing: Boolean
        get() = record.existing != null

    // FR-140: the Cicilan block is offered for a new expense on a credit wallet only
    val offersInstalment: Boolean
        get() = !isEditing && form.type == TransactionType.EXPENSE && catalog.isCreditWallet(form.walletId)

    val instalmentActive: Boolean
        get() = offersInstalment && instalment.enabled

    // FR-039, BR-047: the "Simpan sebagai pintasan" checkbox is offered for a new expense or income only
    val canKeepShortcut: Boolean
        get() = !isEditing && !appliedShortcut && !form.isTransfer && loan == null

    // FR-039: the Pintasan chip is offered on a new draft that is neither a transfer nor a loan
    val offersShortcutChip: Boolean
        get() = !isEditing && isPlainDraft

    private val isPlainDraft: Boolean
        get() = loan == null && !form.isTransfer

    // S-04: Simpan is enabled when the amount is above zero, a wallet and the required route are chosen
    val canSave: Boolean
        get() = form.amount.rupiah > 0L && form.walletId != null && isReadyToSave()

    // S-04: categories of the kind the type needs; a transfer has no category
    val categoryChoices: List<Category>
        get() = form.type.categoryKind()?.let(catalog::categoriesOf) ?: emptyList()

    // S-04: the Pinjaman tab needs a person name; the other tabs need a route and a finished instalment
    private fun isReadyToSave(): Boolean =
        when (val pinjaman = loan) {
            null -> isRouteChosen() && (!instalmentActive || instalment.tenor != null)
            else -> pinjaman.personName.isNotBlank()
        }

    private fun isRouteChosen(): Boolean = if (form.isTransfer) form.targetWalletId != null else form.categoryId != null
}

// S-04: which field the keypad edits, which drawer is open, what the last save found and whether it saved
internal data class SheetStatus(
    val keypadField: KeypadField = KeypadField.AMOUNT,
    val openSheet: EditSheet? = null,
    val errors: Set<TransactionError> = emptySet(),
    val isSaved: Boolean = false,
)

// FR-039, BR-047: the shortcut list, the removed one that can be undone, the checkbox and the errors
internal data class ShortcutState(
    val shortcuts: List<QuickEntry> = emptyList(),
    val removedShortcut: QuickEntry? = null,
    val keepAsShortcut: Boolean = false,
    val appliedShortcut: Boolean = false,
    val shortcutErrors: Set<QuickEntryError> = emptySet(),
)

// FR-140, S-04: the Cicilan block and the Pinjaman block
internal data class ExtraForms(
    val instalment: InstalmentForm = InstalmentForm(),
    val loan: LoanForm? = null,
)
