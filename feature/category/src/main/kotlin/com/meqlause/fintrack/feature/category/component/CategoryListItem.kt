package com.meqlause.fintrack.feature.category.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.feature.category.CategoryCallbacks
import com.meqlause.fintrack.feature.category.R

// FR-021..023: one category row with its actions; an archived row says so
@Composable
internal fun CategoryListItem(
    category: Category,
    callbacks: CategoryCallbacks,
    modifier: Modifier = Modifier,
) {
    val archived = category.state.isArchived
    FintrackCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = category.profile.name,
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        if (archived) {
            Text(
                text = stringResource(R.string.category_archived_label),
                style = FintrackTheme.typography.labelMedium,
                color = FintrackTheme.colors.texts.faint,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium)) {
            ActionLabel(R.string.category_edit) { callbacks.list.onEdit(category) }
            ActionLabel(R.string.category_move_up) { callbacks.list.onMoveUp(category) }
            ActionLabel(R.string.category_move_down) { callbacks.list.onMoveDown(category) }
            ActionLabel(if (archived) R.string.category_restore else R.string.category_archive) {
                callbacks.list.onSetArchived(category, !archived)
            }
            ActionLabel(R.string.category_delete) { callbacks.list.onDelete(category) }
        }
    }
}

// FR-021: a text action; the touch target comes from the card row
@Composable
private fun ActionLabel(
    textRes: Int,
    onClick: () -> Unit,
) {
    FintrackCard(onClick = onClick) {
        Text(
            text = stringResource(textRes),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}
