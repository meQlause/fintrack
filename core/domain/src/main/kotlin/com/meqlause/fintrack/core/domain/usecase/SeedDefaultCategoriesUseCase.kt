package com.meqlause.fintrack.core.domain.usecase

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

/** DOC-02, FR-004: seeds the default categories on first launch, exactly once. */
public class SeedDefaultCategoriesUseCase(
    private val categories: CategoryRepository,
    private val names: DefaultCategoryNames,
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
                    name = names.nameOf(this),
                    kind = kind,
                    appearance = Appearance(iconKey = iconKey, colorKey = colorKey),
                    sortOrder = sortOrder,
                ),
            state = CategoryState(isDefault = true, isArchived = false),
            audit = Audit(createdAt = timestamp, updatedAt = timestamp),
        )
}
