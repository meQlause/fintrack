package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R

// FR-037, S-05: wallet, category and day chips; a chip shows the chosen value and opens its drawer
@Composable
internal fun HistoryFilterChips(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val filter = state.filter
    val dayLabel = filter.dayOfMonth?.let { stringResource(R.string.history_day_label, it) }
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = FintrackTheme.spacing.medium)
                .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        FintrackChip(
            label = state.pickedWalletName() ?: stringResource(R.string.history_filter_wallet),
            selected = filter.walletId != null,
            onClick = { onEvent(HistoryEvent.SheetOpened(HistorySheet.WALLET)) },
        )
        FintrackChip(
            label = state.pickedCategoryName() ?: stringResource(R.string.history_filter_category),
            selected = filter.categoryId != null,
            onClick = { onEvent(HistoryEvent.SheetOpened(HistorySheet.CATEGORY)) },
        )
        FintrackChip(
            label = dayLabel ?: stringResource(R.string.history_filter_day),
            selected = filter.dayOfMonth != null,
            onClick = { onEvent(HistoryEvent.SheetOpened(HistorySheet.DAY)) },
        )
    }
}

// FR-037: the name of the wallet the filter picked, or null while no wallet is picked
private fun HistoryUiState.pickedWalletName(): String? =
    wallets.firstOrNull { it.walletId == filter.walletId }?.profile?.name

// FR-037: the name of the category the filter picked, or null while no category is picked
private fun HistoryUiState.pickedCategoryName(): String? =
    categories.firstOrNull { it.categoryId == filter.categoryId }?.profile?.name
