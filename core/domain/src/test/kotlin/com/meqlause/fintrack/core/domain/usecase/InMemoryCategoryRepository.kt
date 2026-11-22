package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** FR-004: in-memory fake; core:testing cannot be used from this pure JVM module (it depends on it). */
internal class InMemoryCategoryRepository : CategoryRepository {
    private val rows = MutableStateFlow<List<Category>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<Category>> = rows.map { list -> list.sortedBy { it.profile.sortOrder } }

    override suspend fun insert(category: Category): CategoryId {
        lastId += 1
        val stored = category.copy(categoryId = CategoryId(lastId))
        rows.value = rows.value + stored
        return stored.categoryId
    }

    override suspend fun delete(category: Category) {
        rows.value = rows.value.filterNot { it.categoryId == category.categoryId }
    }
}
