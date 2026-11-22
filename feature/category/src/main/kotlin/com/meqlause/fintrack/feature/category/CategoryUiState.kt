package com.meqlause.fintrack.feature.category

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryKind

// FR-020..023: every state of S-10 for the selected tab (Pengeluaran or Pemasukan)
internal sealed interface CategoryUiState {
    // S-10: categories are still being read from the repository
    data object Loading : CategoryUiState

    // S-10: no category of this kind yet; the editor may already be open for the first one
    data class Empty(
        val kind: CategoryKind,
        val editor: CategoryEditor?,
    ) : CategoryUiState

    // S-10: categories in manual order, archived ones included; archiveOffer is set when a delete was blocked (BR-008)
    data class Success(
        val kind: CategoryKind,
        val categories: List<Category>,
        val editor: CategoryEditor?,
        val archiveOffer: Category?,
    ) : CategoryUiState
}
