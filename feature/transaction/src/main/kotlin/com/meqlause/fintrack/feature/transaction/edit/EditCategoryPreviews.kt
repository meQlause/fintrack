package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-030, S-04: the category drawer open on the sample expense, with Makan picked
internal fun categoryListSample(): EditTransactionUiState = expenseSample().showing(EditSheet.CATEGORY)

// FR-021, S-04: the "Tambah kategori" form with a name and the first icon and colour chosen
internal fun categoryFormSample(): EditTransactionUiState =
    categoryListSample().withExtras {
        copy(newCategory = CategoryForm(name = "Kopi", iconKey = "food", colorKey = "orange"))
    }

@Preview(name = "Pilih kategori", showBackground = true)
@Composable
internal fun EditCategoryListPreview() {
    CategoryPreviewCanvas { EditCategoryDrawer(state = categoryListSample(), onEvent = {}) }
}

@Preview(name = "Tambah kategori", showBackground = true)
@Composable
internal fun EditCategoryFormPreview() {
    CategoryPreviewCanvas { EditCategoryDrawer(state = categoryFormSample(), onEvent = {}) }
}

@Composable
private fun CategoryPreviewCanvas(content: @Composable () -> Unit) {
    FintrackTheme(darkTheme = false) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(FintrackTheme.colors.surfaces.background)
                    .padding(FintrackTheme.spacing.large),
        ) {
            content()
        }
    }
}
