package com.meqlause.fintrack.feature.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.BackAction
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.component.HeaderTitle
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.component.ScreenDashedButton
import com.meqlause.fintrack.core.designsystem.component.ScreenSegments
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.feature.category.component.ArchiveOfferCard
import com.meqlause.fintrack.feature.category.component.CategoryEditorSheet
import com.meqlause.fintrack.feature.category.component.CategoryListItem
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-020..023, S-10: stateless Kategori screen; tabs, list, editor and archive offer come from CategoryUiState
@Composable
internal fun CategoryScreen(
    state: CategoryUiState,
    callbacks: CategoryCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        ScreenBackHeader(
            title = HeaderTitle(text = stringResource(R.string.category_title)),
            back = BackAction(label = stringResource(R.string.category_back), onBack = callbacks.onBack),
        )
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = FintrackScreenSizes.screenGutter),
            verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            ScreenSegments(
                labels =
                    listOf(
                        stringResource(R.string.category_tab_expense),
                        stringResource(R.string.category_tab_income),
                    ),
                selected = if (state.kindOrNull() == CategoryKind.INCOME) 1 else 0,
                onSelect = { index -> callbacks.onSelectKind(kindAt(index)) },
            )
            CategoryBody(state = state, callbacks = callbacks, modifier = Modifier.weight(1f))
            ScreenDashedButton(text = stringResource(R.string.category_add), onClick = callbacks.onAdd)
        }
        state.editorOrNull()?.let { draft ->
            CategoryEditorSheet(draft = draft, callbacks = callbacks)
        }
    }
}

// S-10: the list area; Loading shows nothing, Empty shows the empty state, Success shows the rows
@Composable
private fun CategoryBody(
    state: CategoryUiState,
    callbacks: CategoryCallbacks,
    modifier: Modifier,
) {
    when (state) {
        CategoryUiState.Loading -> {
            Column(modifier = modifier) {}
        }

        is CategoryUiState.Empty -> {
            EmptyState(
                copy =
                    EmptyStateCopy(
                        title = stringResource(R.string.category_empty_title),
                        body = stringResource(R.string.category_empty_body),
                    ),
                iconRes = DesignR.drawable.ic_plus,
                modifier = modifier,
            )
        }

        is CategoryUiState.Success -> {
            CategoryList(state = state, callbacks = callbacks, modifier = modifier)
        }
    }
}

// S-10: the archive offer first, then one row per category of the selected kind
@Composable
private fun CategoryList(
    state: CategoryUiState.Success,
    callbacks: CategoryCallbacks,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        state.archiveOffer?.let { offer ->
            item(key = "archive-offer") {
                ArchiveOfferCard(
                    categoryName = offer.profile.name,
                    onConfirm = callbacks.archiveOffer.onConfirm,
                    onDismiss = callbacks.archiveOffer.onDismiss,
                )
            }
        }
        items(state.categories, key = { it.categoryId.value }) { category ->
            CategoryListItem(category = category, callbacks = callbacks)
        }
    }
}

private fun CategoryUiState.kindOrNull(): CategoryKind? =
    when (this) {
        CategoryUiState.Loading -> null
        is CategoryUiState.Empty -> kind
        is CategoryUiState.Success -> kind
    }

private fun CategoryUiState.editorOrNull(): CategoryEditor? =
    when (this) {
        CategoryUiState.Loading -> null
        is CategoryUiState.Empty -> editor
        is CategoryUiState.Success -> editor
    }

// FR-020: segment index 0 is Pengeluaran, 1 is Pemasukan
private fun kindAt(index: Int): CategoryKind = if (index == 1) CategoryKind.INCOME else CategoryKind.EXPENSE
