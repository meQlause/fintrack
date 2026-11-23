package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.quickentry.QuickEntryError
import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.QuickEntry
import java.time.Instant

// S-04: everything the add or edit sheet shows; the ViewModel owns it and the screen only reads it
internal data class EditTransactionUiState(
    val form: TransactionForm = TransactionForm(),
    val catalog: EditCatalog = EditCatalog(),
    val record: EditRecord = EditRecord(existing = null, occurredAt = Instant.EPOCH, dateLabel = ""),
    val keypadField: KeypadField = KeypadField.AMOUNT,
    val openSheet: EditSheet? = null,
    val errors: Set<TransactionError> = emptySet(),
    val isSaved: Boolean = false,
    val shortcuts: List<QuickEntry> = emptyList(),
    val removedShortcut: QuickEntry? = null,
    val keepAsShortcut: Boolean = false,
    val appliedShortcut: Boolean = false,
    val shortcutErrors: Set<QuickEntryError> = emptySet(),
) {
    val isEditing: Boolean
        get() = record.existing != null

    // FR-039, BR-047: the "Simpan sebagai pintasan" checkbox is offered for a new expense or income only
    val canKeepShortcut: Boolean
        get() = !isEditing && !appliedShortcut && !form.isTransfer

    // S-04: Simpan is enabled when the amount is above zero, a wallet and the required route are chosen
    val canSave: Boolean
        get() = form.amount.rupiah > 0L && form.walletId != null && isRouteChosen()

    // S-04: categories of the kind the type needs; a transfer has no category
    val categoryChoices: List<Category>
        get() = form.type.categoryKind()?.let(catalog::categoriesOf) ?: emptyList()

    private fun isRouteChosen(): Boolean = if (form.isTransfer) form.targetWalletId != null else form.categoryId != null
}
