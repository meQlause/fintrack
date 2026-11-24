package com.meqlause.fintrack.feature.transaction.shortcut

import com.meqlause.fintrack.core.domain.quickentry.QuickEntryError
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.transaction.edit.EditCatalog

// FR-039: what the user typed or picked for the shortcut; the amount stays a digit string until it is saved
internal data class ShortcutDraft(
    val name: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val rupiahDigits: String = "",
    val categoryId: CategoryId? = null,
    val walletId: WalletId? = null,
    val note: String = "",
) {
    val amount: Money get() = Money(rupiahDigits.toLongOrNull() ?: 0L)
}

// FR-039: the drawer that replaces the form while one of the pickers is open
internal enum class ShortcutSheet {
    CATEGORY,
    WALLET,
}

// FR-039: which shortcut is edited, the open drawer, what the last save found and whether it saved
internal data class ShortcutFormStatus(
    val editedId: QuickEntryId? = null,
    val openSheet: ShortcutSheet? = null,
    val errors: Set<QuickEntryError> = emptySet(),
    val isSaved: Boolean = false,
)

// FR-039, S-03: everything the "Pintasan baru" form shows; the ViewModel owns it and the screen only reads it
internal data class ShortcutFormUiState(
    val draft: ShortcutDraft = ShortcutDraft(),
    val catalog: EditCatalog = EditCatalog(),
    val status: ShortcutFormStatus = ShortcutFormStatus(),
) {
    val isEditing: Boolean get() = status.editedId != null
    val openSheet: ShortcutSheet? get() = status.openSheet

    // BR-006: the categories of the kind the type needs
    val categoryChoices: List<Category>
        get() = catalog.categoriesOf(draft.type.shortcutKind())

    // FR-039: Simpan needs a name, an amount, a category and a wallet
    val canSave: Boolean
        get() =
            draft.name.isNotBlank() && draft.amount.rupiah > 0L && draft.categoryId != null && draft.walletId != null

    // FR-039: one change of the editor state
    fun withStatus(change: ShortcutFormStatus.() -> ShortcutFormStatus): ShortcutFormUiState =
        copy(status = status.change())

    // FR-039: one change of the draft
    fun withDraft(change: ShortcutDraft.() -> ShortcutDraft): ShortcutFormUiState = copy(draft = draft.change())
}
