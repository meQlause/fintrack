package com.meqlause.fintrack.feature.category.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.category.R

// FR-022, BR-008: shown when a delete was blocked because a transaction or budget uses the category
@Composable
internal fun ArchiveOfferCard(
    categoryName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FintrackCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
            Text(
                text = stringResource(R.string.category_archive_offer_title),
                style = FintrackTheme.typography.titleMedium,
                color = FintrackTheme.colors.texts.onSurface,
            )
            Text(
                text = stringResource(R.string.category_archive_offer_body, categoryName),
                style = FintrackTheme.typography.bodyMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
            FintrackButton(text = stringResource(R.string.category_archive_offer_confirm), onClick = onConfirm)
            FintrackButton(text = stringResource(R.string.category_archive_offer_dismiss), onClick = onDismiss)
        }
    }
}
