package com.meqlause.fintrack.feature.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import java.time.Instant

// S-10: previews of every state, light and dark
private val previewAudit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH)

private fun previewCategory(
    itemId: Long,
    name: String,
    archived: Boolean = false,
): Category =
    Category(
        categoryId = CategoryId(itemId),
        profile =
            CategoryProfile(
                name = name,
                kind = CategoryKind.EXPENSE,
                appearance = Appearance(iconKey = "food", colorKey = "orange"),
                sortOrder = itemId.toInt(),
            ),
        state = CategoryState(isDefault = true, isArchived = archived),
        audit = previewAudit,
    )

@Composable
private fun PreviewBody(state: CategoryUiState) {
    Column(
        modifier =
            androidx.compose.ui.Modifier
                .fillMaxSize(),
    ) {
        CategoryScreen(state = state, callbacks = CategoryCallbacks())
    }
}

@Composable
private fun SuccessSample() {
    PreviewBody(
        CategoryUiState.Success(
            kind = CategoryKind.EXPENSE,
            categories = listOf(previewCategory(1, "Makanan"), previewCategory(2, "Transportasi", archived = true)),
            editor = null,
            archiveOffer = previewCategory(3, "Tagihan"),
        ),
    )
}

@Composable
private fun EmptySample() {
    PreviewBody(CategoryUiState.Empty(kind = CategoryKind.INCOME, editor = null))
}

@Preview(name = "Kategori success light", showBackground = true, heightDp = 720)
@Composable
internal fun CategorySuccessLightPreview() {
    FintrackTheme(darkTheme = false) { SuccessSample() }
}

@Preview(name = "Kategori success dark", showBackground = true, heightDp = 720)
@Composable
internal fun CategorySuccessDarkPreview() {
    FintrackTheme(darkTheme = true) { SuccessSample() }
}

@Preview(name = "Kategori empty light", showBackground = true, heightDp = 720)
@Composable
internal fun CategoryEmptyLightPreview() {
    FintrackTheme(darkTheme = false) { EmptySample() }
}

@Preview(name = "Kategori empty dark", showBackground = true, heightDp = 720)
@Composable
internal fun CategoryEmptyDarkPreview() {
    FintrackTheme(darkTheme = true) { EmptySample() }
}
