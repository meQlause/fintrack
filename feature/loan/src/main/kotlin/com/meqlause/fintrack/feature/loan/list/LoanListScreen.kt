package com.meqlause.fintrack.feature.loan.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.feature.loan.R

// S-17, FR-153: the loan list with its Piutang and Hutang segments; stateless, previews drive it with plain values
@Composable
internal fun LoanListScreen(
    state: LoanListUiState,
    onBack: () -> Unit,
    onPickDirection: (LoanDirection) -> Unit,
    onOpenLoan: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        Text(
            text = stringResource(R.string.loan_back),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.brands.primary,
            modifier = Modifier.clickable(onClick = onBack).padding(vertical = FintrackTheme.spacing.small),
        )
        Text(
            text = stringResource(R.string.loan_title),
            style = FintrackTheme.typography.headlineSmall,
            color = FintrackTheme.colors.texts.onSurface,
        )
        DirectionChips(selected = state.direction, onPick = onPickDirection)
        LoanListItems(state = state, onOpenLoan = onOpenLoan)
    }
}

// S-17: the Piutang and Hutang segments
@Composable
private fun DirectionChips(
    selected: LoanDirection,
    onPick: (LoanDirection) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FintrackChip(
            label = stringResource(R.string.loan_tab_lent),
            selected = selected == LoanDirection.LENT,
            onClick = { onPick(LoanDirection.LENT) },
        )
        FintrackChip(
            label = stringResource(R.string.loan_tab_borrowed),
            selected = selected == LoanDirection.BORROWED,
            onClick = { onPick(LoanDirection.BORROWED) },
        )
    }
}

// S-17: the open loans grouped by person, then the settled ones; an empty note when there is nothing
@Composable
private fun LoanListItems(
    state: LoanListUiState,
    onOpenLoan: (Long) -> Unit,
) {
    if (state.groups.isEmpty() && state.settled.isEmpty()) {
        Text(
            text = stringResource(R.string.loan_list_empty),
            style = FintrackTheme.typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        items(items = state.groups, key = { it.personName }) { group ->
            PersonGroupItem(group = group, onOpenLoan = onOpenLoan)
        }
        if (state.settled.isNotEmpty()) {
            item(key = "settled") {
                Text(
                    text = stringResource(R.string.loan_section_settled),
                    style = FintrackTheme.typography.labelMedium,
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                )
            }
            items(items = state.settled, key = { it.loanId }) { row ->
                LoanRowItem(loanRow = row, onClick = { onOpenLoan(row.loanId) })
            }
        }
    }
}
