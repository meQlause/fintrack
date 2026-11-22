package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-004: access to expense and income categories. */
public interface CategoryRepository {
    /** FR-004: every category in manual order, archived ones included. */
    public fun observeAll(): Flow<List<Category>>

    /** FR-004: stores a category and returns its generated id; the categoryId of [category] is ignored. */
    public suspend fun insert(category: Category): CategoryId

    /** FR-021, FR-023: stores name, appearance, state and sort order of an existing category. */
    public suspend fun update(category: Category)

    /** BR-008: true while a transaction or budget refers to the category. */
    public suspend fun isInUse(categoryId: CategoryId): Boolean

    /** BR-008: removes a category that no row refers to. */
    public suspend fun delete(category: Category)
}
