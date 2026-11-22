package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.category.DefaultCategory
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant

/**
 * DOC-02, FR-004: seeds the default categories on first launch, exactly once. [nameOf] supplies the Indonesian
 * display names, which the platform reads from string resources (core:domain cannot read Android resources).
 */
public class SeedDefaultCategoriesUseCase(
    private val categories: CategoryRepository,
    private val nameOf: (DefaultCategory) -> String,
    private val clock: Clock,
) {
    /**
     * FR-004: inserts every [DefaultCategory] when no category exists yet. When the table already has
     * categories, which is every run after the first, this does nothing.
     */
    public suspend operator fun invoke() {
        if (categories.observeAll().first().isNotEmpty()) return
        val timestamp = clock.instant()
        DefaultCategory.entries.forEachIndexed { index, category ->
            categories.insert(category.toCategory(sortOrder = index, timestamp = timestamp))
        }
    }

    private fun DefaultCategory.toCategory(
        sortOrder: Int,
        timestamp: Instant,
    ): Category =
        Category(
            categoryId = CategoryId(0L),
            profile =
                CategoryProfile(
                    name = nameOf(this),
                    kind = kind,
                    appearance = Appearance(iconKey = iconKey, colorKey = colorKey),
                    sortOrder = sortOrder,
                ),
            state = CategoryState(isDefault = true, isArchived = false),
            audit = Audit(createdAt = timestamp, updatedAt = timestamp),
        )
}
