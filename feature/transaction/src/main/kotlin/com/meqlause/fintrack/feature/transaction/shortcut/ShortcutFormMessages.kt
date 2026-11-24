package com.meqlause.fintrack.feature.transaction.shortcut

import com.meqlause.fintrack.core.domain.quickentry.QuickEntryError
import com.meqlause.fintrack.feature.transaction.R

// FR-039, BR-047: the Indonesian message of each rule a shortcut can break
private val MESSAGES: Map<QuickEntryError, Int> =
    mapOf(
        QuickEntryError.TITLE_REQUIRED to R.string.shortcut_error_name_required,
        QuickEntryError.TITLE_TOO_LONG to R.string.shortcut_error_name_long,
        QuickEntryError.NOTE_TOO_LONG to R.string.shortcut_error_note_long,
        QuickEntryError.TYPE_NOT_SHORTCUT to R.string.shortcut_error_type,
        QuickEntryError.AMOUNT_INVALID to R.string.shortcut_error_amount,
        QuickEntryError.CATEGORY_KIND_MISMATCH to R.string.shortcut_error_category,
        QuickEntryError.WALLET_NOT_ALLOWED to R.string.shortcut_error_wallet,
    )

// FR-039: the message of one rule
internal fun QuickEntryError.messageRes(): Int = MESSAGES.getValue(this)
