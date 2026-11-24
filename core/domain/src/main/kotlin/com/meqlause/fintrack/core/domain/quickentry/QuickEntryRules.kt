package com.meqlause.fintrack.core.domain.quickentry

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletType

private const val MAX_TITLE_LENGTH = 24
private const val MAX_NOTE_LENGTH = 120

// FR-039, BR-047: a shortcut is an expense or income with a 1 to 24 character name and a valid amount
internal object QuickEntryRules {
    private val shortcutTypes = setOf(TransactionType.EXPENSE, TransactionType.INCOME)
    private val creditTypes = setOf(WalletType.CREDIT_CARD, WalletType.PAYLATER)
    private val kindOf =
        mapOf(TransactionType.EXPENSE to CategoryKind.EXPENSE, TransactionType.INCOME to CategoryKind.INCOME)

    // FR-039: every rule the draft breaks; an empty set means the shortcut may be stored
    fun check(
        draft: QuickEntry,
        category: Category?,
        wallet: Wallet?,
    ): Set<QuickEntryError> =
        buildSet {
            val title = draft.template.title.trim()
            if (title.isEmpty()) add(QuickEntryError.TITLE_REQUIRED)
            if (title.length > MAX_TITLE_LENGTH) add(QuickEntryError.TITLE_TOO_LONG)
            val note = draft.template.note.trim()
            if (note.length > MAX_NOTE_LENGTH) add(QuickEntryError.NOTE_TOO_LONG)
            if (draft.template.type !in shortcutTypes) add(QuickEntryError.TYPE_NOT_SHORTCUT)
            if (draft.template.amount.rupiah !in 1L..Money.MAX_TRANSACTION_RUPIAH) add(QuickEntryError.AMOUNT_INVALID)
            if (isKindMismatch(draft.template.type, category)) add(QuickEntryError.CATEGORY_KIND_MISMATCH)
            if (isCreditIncome(draft.template.type, wallet)) add(QuickEntryError.WALLET_NOT_ALLOWED)
        }

    // BR-006: an expense needs an expense category and an income an income category; an unknown one is left to the FK
    private fun isKindMismatch(
        type: TransactionType,
        category: Category?,
    ): Boolean = category != null && kindOf[type] != category.profile.kind

    // FR-039: income can only land in a non-credit wallet (BR-039)
    private fun isCreditIncome(
        type: TransactionType,
        wallet: Wallet?,
    ): Boolean = type == TransactionType.INCOME && wallet?.kind?.type in creditTypes
}
