package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.PickRowText
import com.meqlause.fintrack.core.designsystem.component.SheetPickRow
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R

// FR-030, FR-031, S-04: "Kategori" over the picked name with its tinted tile and a chevron; opens the drawer
@Composable
internal fun EditCategoryCard(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val picked = state.catalog.categories.firstOrNull { it.categoryId == state.form.categoryId }
    val appearance = picked?.profile?.appearance
    SheetPickRow(
        text =
            PickRowText(
                label = stringResource(R.string.transaction_category_label),
                name = picked?.profile?.name ?: stringResource(R.string.transaction_category_pick),
            ),
        look = IconLook(iconKey = appearance?.iconKey ?: "other", colorKey = appearance?.colorKey ?: "gray"),
        onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.CATEGORY)) },
        modifier = modifier.padding(top = FintrackTheme.spacing.medium),
    )
}
