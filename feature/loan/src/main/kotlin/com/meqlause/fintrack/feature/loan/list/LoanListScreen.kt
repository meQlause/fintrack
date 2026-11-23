package com.meqlause.fintrack.feature.loan.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.BackAction
import com.meqlause.fintrack.core.designsystem.component.HeaderTitle
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenSegments
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.loan.R

// S-17, FR-153: the loan list with its Piutang and Hutang segments; stateless, previews drive it with plain values
@Composable
internal fun LoanListScreen(
    state: LoanListUiState,
    callbacks: LoanListCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        ScreenBackHeader(
            title = HeaderTitle(text = stringResource(R.string.loan_title)),
            back = BackAction(label = stringResource(R.string.loan_back), onBack = callbacks.onBack),
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = FintrackScreenSizes.screenGutter),
            contentPadding = PaddingValues(top = FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall),
        ) {
            item { DirectionSegments(selected = state.direction, onPick = callbacks.onPickDirection) }
            item { LoanTotal(state = state) }
            item { OpenLoans(state = state, onOpenLoan = callbacks.onOpenLoan) }
            item { SettledLoans(state = state, onOpenLoan = callbacks.onOpenLoan) }
        }
    }
}

// S-17: the Piutang and Hutang segments
@Composable
private fun DirectionSegments(
    selected: LoanDirection,
    onPick: (LoanDirection) -> Unit,
) {
    ScreenSegments(
        labels = listOf(stringResource(R.string.loan_tab_lent), stringResource(R.string.loan_tab_borrowed)),
        selected = if (selected == LoanDirection.LENT) 0 else 1,
        onSelect = { onPick(if (it == 0) LoanDirection.LENT else LoanDirection.BORROWED) },
    )
}

// S-17: "Total piutang" or "Total hutang" with the sum still outstanding, 18 px under the segments
@Composable
private fun LoanTotal(state: LoanListUiState) {
    val total = state.groups.sumOf { group -> group.rows.sumOf { it.outstanding.rupiah } }
    Column(modifier = Modifier.padding(top = FintrackTheme.spacing.large + FintrackTheme.spacing.xxSmall)) {
        Text(
            text =
                stringResource(
                    if (state.direction ==
                        LoanDirection.LENT
                    ) {
                        R.string.loan_total_lent
                    } else {
                        R.string.loan_total_borrowed
                    },
                ),
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = MoneyFormatter.format(Money(total)),
            style = FintrackScreenText.number(size = 28f, weight = 700),
            color = FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
    }
}

// S-17: the open loans in one card, or "Tidak ada" when there are none
@Composable
private fun OpenLoans(
    state: LoanListUiState,
    onOpenLoan: (Long) -> Unit,
) {
    val rows = state.groups.flatMap { it.rows }
    ScreenCard(modifier = Modifier.padding(top = FintrackScreenSizes.rowPaddingHorizontal)) {
        rows.forEachIndexed { index, row ->
            LoanRowItem(loanRow = row, showDivider = index > 0, onClick = { onOpenLoan(row.loanId) })
        }
        if (rows.isEmpty()) {
            Text(
                text = stringResource(R.string.loan_list_empty),
                style = FintrackScreenText.style(size = 13f, weight = 400),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(FintrackScreenSizes.billInset),
            )
        }
    }
}

// S-17: the settled loans under "Selesai", each a muted line with its status
@Composable
private fun SettledLoans(
    state: LoanListUiState,
    onOpenLoan: (Long) -> Unit,
) {
    if (state.settled.isEmpty()) return
    Column(modifier = Modifier.padding(top = FintrackScreenSizes.groupGap)) {
        Text(
            text = stringResource(R.string.loan_section_settled),
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier = Modifier.padding(bottom = FintrackScreenSizes.groupLabelBottom),
        )
        ScreenCard {
            state.settled.forEachIndexed { index, row ->
                LoanRowItem(loanRow = row, showDivider = index > 0, onClick = { onOpenLoan(row.loanId) })
            }
        }
    }
}
