package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.quickentry.QuickEntryResult
import com.meqlause.fintrack.core.domain.quickentry.QuickEntryRules
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.QuickEntry
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** FR-039, BR-047: stores or edits a shortcut after its rules pass; a shortcut never creates a transaction. */
public class SaveQuickEntryUseCase
    @Inject
    constructor(
        private val quickEntries: QuickEntryRepository,
        private val categories: CategoryRepository,
        private val wallets: WalletRepository,
    ) {
        /** FR-039: a new shortcut goes last, an edited one keeps its place; a broken rule stores nothing. */
        public suspend operator fun invoke(draft: QuickEntry): QuickEntryResult {
            val category = categories.observeAll().first().firstOrNull { it.categoryId == draft.categoryId }
            val wallet = wallets.observeAll().first().firstOrNull { it.walletId == draft.walletId }
            val errors = QuickEntryRules.check(draft, category, wallet)
            if (errors.isNotEmpty()) return QuickEntryResult.Rejected(errors)
            val stored = quickEntries.observeAll().first()
            val clean = draft.withTrimmedText()
            val existing = stored.firstOrNull { it.quickEntryId == draft.quickEntryId }
            return QuickEntryResult.Saved(if (existing == null) insertLast(clean, stored) else edit(clean, existing))
        }

        private suspend fun insertLast(
            draft: QuickEntry,
            stored: List<QuickEntry>,
        ) = quickEntries.insert(draft.copy(sortOrder = (stored.maxOfOrNull { it.sortOrder } ?: -1) + 1))

        // FR-039: editing keeps the position and the creation time of the stored shortcut
        private suspend fun edit(
            draft: QuickEntry,
            existing: QuickEntry,
        ) = existing.quickEntryId.also {
            val audit = draft.audit.copy(createdAt = existing.audit.createdAt)
            quickEntries.update(draft.copy(sortOrder = existing.sortOrder, audit = audit))
        }

        private fun QuickEntry.withTrimmedText(): QuickEntry =
            copy(template = template.copy(title = template.title.trim(), note = template.note.trim()))
    }
