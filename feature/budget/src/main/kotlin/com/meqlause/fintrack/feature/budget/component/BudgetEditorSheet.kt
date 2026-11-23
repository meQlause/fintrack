package com.meqlause.fintrack.feature.budget.component

import androidx.compose.runtime.Composable
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.feature.budget.BudgetCallbacks
import com.meqlause.fintrack.feature.budget.BudgetEditor

// FR-070, S-11: the sheet for choosing a target and typing its limit; the state decides which one shows
@Composable
internal fun BudgetEditorSheet(
    editor: BudgetEditor,
    categories: List<Category>,
    callbacks: BudgetCallbacks,
) {
    when (editor) {
        BudgetEditor.Pick -> BudgetPickerDrawer(categories = categories, callbacks = callbacks)
        is BudgetEditor.Amount -> BudgetAmountDrawer(editor = editor, callbacks = callbacks)
    }
}
