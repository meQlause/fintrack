package com.meqlause.fintrack.feature.category

import com.meqlause.fintrack.core.model.CategoryKind

// DOC-03: every user action of S-10, passed to the stateless screen as plain lambdas; defaults do nothing
internal data class CategoryCallbacks(
    val onBack: () -> Unit = {},
    val onSelectKind: (CategoryKind) -> Unit = {},
    val onAdd: () -> Unit = {},
    val list: CategoryListCallbacks = CategoryListCallbacks(),
    val editor: CategoryEditorCallbacks = CategoryEditorCallbacks(),
    val archiveOffer: ArchiveOfferCallbacks = ArchiveOfferCallbacks(),
)
