package com.meqlause.fintrack.feature.budget

import com.meqlause.fintrack.core.model.CategoryId

// FR-074: what a limit applies to; a null categoryId is the overall budget, a null name is shown as "Semua pengeluaran"
internal data class BudgetTarget(
    val categoryId: CategoryId?,
    val categoryName: String?,
)
