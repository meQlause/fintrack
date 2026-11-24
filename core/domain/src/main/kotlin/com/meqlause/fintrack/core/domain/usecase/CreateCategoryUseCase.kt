package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.category.CategoryResult
import com.meqlause.fintrack.core.domain.category.CategoryRules
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/** FR-021: stores a new category after its name rules pass; the seeded defaults are never touched. */
public class CreateCategoryUseCase
    @Inject
    constructor(
        private val categories: CategoryRepository,
        private val clock: Clock,
    ) {
        /** FR-021: the category is appended after the existing ones; nothing is stored when a rule is broken. */
        public suspend operator fun invoke(draft: CategoryProfile): CategoryResult {
            val stored = categories.observeAll().first()
            val errors = CategoryRules.check(draft, stored)
            if (errors.isNotEmpty()) return CategoryResult.Rejected(errors)
            val stamp = Instant.now(clock)
            val sortOrder = (stored.maxOfOrNull { it.profile.sortOrder } ?: -1) + 1
            val category =
                Category(
                    categoryId = CategoryId(0L),
                    profile = draft.copy(name = draft.name.trim(), sortOrder = sortOrder),
                    state = CategoryState(isDefault = false, isArchived = false),
                    audit = Audit(createdAt = stamp, updatedAt = stamp),
                )
            return CategoryResult.Created(categories.insert(category))
        }
    }
