package com.meqlause.fintrack.feature.credit.instalment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.BackAction
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.component.HeaderTitle
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.credit.R

// FR-142, S-16: the instalment list with its Aktif and Selesai segments; stateless, previews drive it with plain values
@Composable
internal fun InstalmentListScreen(
    state: InstalmentListUiState,
    onBack: () -> Unit,
    onPickFilter: (InstalmentFilter) -> Unit,
    onOpenInstalment: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        ScreenBackHeader(
            title = HeaderTitle(text = stringResource(R.string.instalment_list_title)),
            back = BackAction(label = stringResource(R.string.credit_back), onBack = onBack),
        )
        if (state is InstalmentListUiState.Success) {
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = FintrackScreenSizes.screenGutter),
                verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
            ) {
                InstalmentListBody(state = state, onPickFilter = onPickFilter, onOpenInstalment = onOpenInstalment)
            }
        }
    }
}

// S-16: the segment chips, then the plans of the chosen segment, or an empty note
@Composable
private fun ColumnScope.InstalmentListBody(
    state: InstalmentListUiState.Success,
    onPickFilter: (InstalmentFilter) -> Unit,
    onOpenInstalment: (Long) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        InstalmentFilter.entries.forEach { filter ->
            FintrackChip(
                label = stringResource(filterLabel(filter)),
                selected = state.filter == filter,
                onClick = { onPickFilter(filter) },
            )
        }
    }
    if (state.rows.isEmpty()) {
        Text(
            text = stringResource(R.string.instalment_list_empty),
            style = FintrackTheme.typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            items(items = state.rows, key = { it.planId }) { row ->
                InstalmentListItem(instalmentRow = row, onClick = { onOpenInstalment(row.planId) })
            }
        }
    }
}

private fun filterLabel(filter: InstalmentFilter): Int =
    if (filter == InstalmentFilter.ACTIVE) R.string.instalment_filter_active else R.string.instalment_filter_finished
