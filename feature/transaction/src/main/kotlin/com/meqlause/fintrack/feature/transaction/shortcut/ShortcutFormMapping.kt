package com.meqlause.fintrack.feature.transaction.shortcut

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.QuickTemplate
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import java.time.Instant

// BR-006: an income shortcut needs an income category, an expense shortcut an expense one
internal fun TransactionType.shortcutKind(): CategoryKind =
    if (this == TransactionType.INCOME) CategoryKind.INCOME else CategoryKind.EXPENSE

// FR-039: the shortcut the form would store; null while the category or wallet is still missing
internal fun ShortcutFormUiState.toQuickEntry(currentTime: Instant): QuickEntry? {
    val categoryId = draft.categoryId
    val walletId = draft.walletId
    return if (categoryId == null || walletId == null) {
        null
    } else {
        QuickEntry(
            quickEntryId = status.editedId ?: QuickEntryId(0L),
            template = QuickTemplate(draft.name, draft.type, draft.amount, draft.note),
            categoryId = categoryId,
            walletId = walletId,
            sortOrder = 0,
            audit = Audit(createdAt = currentTime, updatedAt = currentTime),
        )
    }
}

// FR-039: a stored shortcut loaded into the form
internal fun QuickEntry.toDraft(): ShortcutDraft =
    ShortcutDraft(
        name = template.title,
        type = template.type,
        rupiahDigits = template.amount.rupiah.toString(),
        categoryId = categoryId,
        walletId = walletId,
        note = template.note,
    )

// BR-006, BR-039: switching the type drops a category of the other kind and, for income, a credit wallet
internal fun ShortcutFormUiState.withType(type: TransactionType): ShortcutFormUiState {
    val pickedKind =
        catalog.categories
            .firstOrNull { it.categoryId == draft.categoryId }
            ?.profile
            ?.kind
    val keepWallet = type == TransactionType.EXPENSE || !catalog.isCreditWallet(draft.walletId)
    return withDraft {
        copy(
            type = type,
            categoryId = categoryId.takeIf { pickedKind == type.shortcutKind() },
            walletId = walletId.takeIf { keepWallet },
        )
    }
}

// FR-039: a new shortcut starts on the first wallet that suits its type, one tap fewer for the user
internal fun ShortcutFormUiState.withDefaultWallet(): ShortcutFormUiState {
    if (isEditing || draft.walletId != null) return this
    val wallet = catalog.activeWallets().firstOrNull { suitsType(it.walletId) }
    return withDraft { copy(walletId = wallet?.walletId) }
}

// BR-039: an expense can use any wallet, income only a non-credit one
private fun ShortcutFormUiState.suitsType(walletId: WalletId): Boolean =
    draft.type == TransactionType.EXPENSE || !catalog.isCreditWallet(walletId)
