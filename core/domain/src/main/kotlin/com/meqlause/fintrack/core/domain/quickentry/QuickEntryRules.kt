package com.meqlause.fintrack.core.domain.quickentry

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.TransactionType

private const val MAX_TITLE_LENGTH = 24

// FR-039, BR-047: a shortcut is an expense or income with a 1 to 24 character title and a valid amount
internal object QuickEntryRules {
    private val shortcutTypes = setOf(TransactionType.EXPENSE, TransactionType.INCOME)

    // FR-039: every rule the draft breaks; an empty set means the shortcut may be stored
    fun check(draft: QuickEntry): Set<QuickEntryError> =
        buildSet {
            val title = draft.template.title.trim()
            if (title.isEmpty()) add(QuickEntryError.TITLE_REQUIRED)
            if (title.length > MAX_TITLE_LENGTH) add(QuickEntryError.TITLE_TOO_LONG)
            if (draft.template.type !in shortcutTypes) add(QuickEntryError.TYPE_NOT_SHORTCUT)
            if (draft.template.amount.rupiah !in 1L..Money.MAX_TRANSACTION_RUPIAH) add(QuickEntryError.AMOUNT_INVALID)
        }
}
