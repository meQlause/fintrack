package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.category.CategoryError

// FR-021, S-04: the "Tambah kategori" form inside the category drawer; the kind comes from the open type tab
internal data class CategoryForm(
    val name: String = "",
    val iconKey: String = "other",
    val colorKey: String = "gray",
    val errors: Set<CategoryError> = emptySet(),
)
