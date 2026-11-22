package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.CategoryEntity
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState

// DOC-02, FR-004: maps a category row to the domain model
internal fun CategoryEntity.toCategory(): Category =
    Category(
        categoryId = CategoryId(categoryId),
        profile =
            CategoryProfile(
                name = identity.name,
                kind = identity.kind,
                appearance = appearance.toAppearance(),
                sortOrder = state.sortOrder,
            ),
        state = CategoryState(isDefault = state.isDefault, isArchived = state.isArchived),
        audit = audit.toAudit(),
    )

// DOC-02, FR-004: maps a category to its row; the kind is stored and never changed (BR-006)
internal fun Category.toEntity(): CategoryEntity =
    CategoryEntity(
        categoryId = categoryId.value,
        identity = CategoryEntity.Identity(name = profile.name, kind = profile.kind),
        appearance = profile.appearance.toColumns(),
        state =
            CategoryEntity.State(
                isDefault = state.isDefault,
                isArchived = state.isArchived,
                sortOrder = profile.sortOrder,
            ),
        audit = audit.toColumns(),
    )
