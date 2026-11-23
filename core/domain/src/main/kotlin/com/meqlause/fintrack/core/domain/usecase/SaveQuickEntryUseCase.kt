package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.quickentry.QuickEntryResult
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryRules
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.model.QuickEntry
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** FR-039, BR-047: stores a shortcut after its rules pass; a shortcut never creates a transaction by itself. */
public class SaveQuickEntryUseCase
    @Inject
    constructor(
        private val quickEntries: QuickEntryRepository,
    ) {
        /** FR-039: the new shortcut is appended after the existing ones; nothing is stored when a rule is broken. */
        public suspend operator fun invoke(draft: QuickEntry): QuickEntryResult {
            val errors = QuickEntryRules.check(draft)
            if (errors.isNotEmpty()) return QuickEntryResult.Rejected(errors)
            val nextOrder = (quickEntries.observeAll().first().maxOfOrNull { it.sortOrder } ?: -1) + 1
            val title = draft.template.title.trim()
            val stored = draft.copy(template = draft.template.copy(title = title), sortOrder = nextOrder)
            return QuickEntryResult.Saved(quickEntries.insert(stored))
        }
    }
