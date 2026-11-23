package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId

private const val MAX_NOTE_LENGTH = 120

// S-04: a transfer keeps a target wallet; a category is kept only while its kind still fits the type
internal fun EditTransactionUiState.withType(type: TransactionType): EditTransactionUiState {
    val kind = type.categoryKind()
    val fitsKind = catalog.categories.any { it.categoryId == form.categoryId && it.profile.kind == kind }
    val edited =
        form.copy(type = type).withRoute {
            copy(
                targetWalletId = targetWalletId.takeIf { type == TransactionType.TRANSFER },
                categoryId = categoryId.takeIf { fitsKind },
            )
        }
    return copy(form = edited).withStatus { copy(errors = emptySet()) }.withExtras { copy(loan = null) }
}

// S-04: the drawer decides which side of the sheet the picked wallet belongs to
internal fun EditTransactionUiState.withPickedWallet(walletId: WalletId): EditTransactionUiState {
    val side = if (openSheet == EditSheet.TARGET_WALLET) EditSheet.TARGET_WALLET else EditSheet.WALLET
    val edited =
        form.withRoute {
            if (side == EditSheet.TARGET_WALLET) copy(targetWalletId = walletId) else copy(walletId = walletId)
        }
    return copy(form = edited).withStatus { copy(errors = emptySet(), openSheet = null) }
}

// S-04: the drawer that is open, or none
internal fun EditTransactionUiState.showing(sheet: EditSheet?): EditTransactionUiState =
    withStatus { copy(openSheet = sheet) }

// S-04: a change of the draft; the errors shown so far no longer apply
internal fun EditTransactionUiState.withForm(change: (TransactionForm) -> TransactionForm): EditTransactionUiState =
    copy(form = change(form)).withStatus { copy(errors = emptySet()) }

// S-04: the category picked for the draft
internal fun EditTransactionUiState.withCategory(categoryId: CategoryId?): EditTransactionUiState =
    withForm { it.withRoute { copy(categoryId = categoryId) } }

// S-04: the wallet and the target wallet trade places
internal fun EditTransactionUiState.withSwappedWallets(): EditTransactionUiState =
    withForm { it.withRoute { copy(walletId = targetWalletId, targetWalletId = walletId) } }

// S-04: the note, as far as the field accepts
internal fun EditTransactionUiState.withNote(note: String): EditTransactionUiState =
    withForm { it.copy(note = note.take(MAX_NOTE_LENGTH)) }
