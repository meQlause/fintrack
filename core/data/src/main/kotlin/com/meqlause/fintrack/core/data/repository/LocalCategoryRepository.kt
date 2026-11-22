package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toCategory
import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.database.dao.CategoryDao
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-004: categories in Room; a category that is still referenced cannot be deleted (BR-008). */
public class LocalCategoryRepository(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
    override fun observeAll(): Flow<List<Category>> =
        categoryDao.observeAll().map { rows ->
            rows.map { it.toCategory() }
        }

    override suspend fun insert(category: Category): CategoryId = CategoryId(categoryDao.insert(category.toEntity()))

    override suspend fun delete(category: Category) {
        categoryDao.delete(category.toEntity())
    }
}
