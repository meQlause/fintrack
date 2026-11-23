package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.icon.categoryColorOf
import com.meqlause.fintrack.core.domain.statistics.CategoryChange
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.CategoryId

private const val UNCATEGORIZED_COLOR_KEY = "gray"

// S-08: a category's slice, bar and dot take the category's own colour; uncategorised spending is gray
@Composable
@ReadOnlyComposable
internal fun statisticsColorOf(
    change: CategoryChange,
    appearances: Map<CategoryId, Appearance>,
): Color {
    val key = change.categoryId?.let { appearances[it]?.colorKey } ?: UNCATEGORIZED_COLOR_KEY
    return categoryColorOf(key)
}
