package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import kotlinx.coroutines.flow.Flow

/** ADR-007, FR-004: in-memory categories ordered by manual sort order. */
public class FakeCategoryRepository : CategoryRepository {
    private val store =
        RowStore<Category>(
            idOf = { it.categoryId.value },
            withId = { category, newId -> category.copy(categoryId = CategoryId(newId)) },
            order = compareBy { it.profile.sortOrder },
        )

    override fun observeAll(): Flow<List<Category>> = store.observe()

    override suspend fun insert(category: Category): CategoryId = CategoryId(store.append(category))

    override suspend fun delete(category: Category) {
        store.remove(category.categoryId.value)
    }
}
