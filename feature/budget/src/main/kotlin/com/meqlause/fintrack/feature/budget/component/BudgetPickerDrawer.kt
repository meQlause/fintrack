package com.meqlause.fintrack.feature.budget.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.feature.budget.BudgetCallbacks
import com.meqlause.fintrack.feature.budget.BudgetTarget
import com.meqlause.fintrack.feature.budget.R

// FR-070, FR-074: chooses the overall budget or one expense category; income categories are never offered (FR-070)
@Composable
internal fun BudgetPickerDrawer(
    categories: List<Category>,
    callbacks: BudgetCallbacks,
) {
    PickerDrawer(title = stringResource(R.string.budget_pick_title), onClose = callbacks.editor.onDismissEditor) {
        Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
            PickerRow(label = stringResource(R.string.budget_overall)) {
                callbacks.onPick(BudgetTarget(categoryId = null, categoryName = null))
            }
            categories.forEach { category ->
                PickerRow(label = category.profile.name) {
                    callbacks.onPick(BudgetTarget(category.categoryId, category.profile.name))
                }
            }
        }
    }
}

// S-11: one tappable choice in the picker
@Composable
private fun PickerRow(
    label: String,
    onClick: () -> Unit,
) {
    FintrackCard(onClick = onClick) {
        Text(text = label, style = FintrackTheme.typography.labelLarge, color = FintrackTheme.colors.texts.onSurface)
    }
}
