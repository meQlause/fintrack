package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// FR-137, FR-146, S-03: the Tagihan card; a bill opens its credit wallet (S-14), the footer cells open S-16 and S-17
@Composable
internal fun HomeBillsCard(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    FintrackCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues()) {
        Text(
            text = stringResource(R.string.home_bills_title),
            modifier = Modifier.padding(start = FintrackTheme.spacing.large, top = FintrackTheme.spacing.stack),
            style = FintrackTheme.typography.caption,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        state.bills.bills.forEach { bill ->
            HorizontalDivider(thickness = FintrackTheme.sizes.hairline, color = FintrackTheme.colors.surfaces.outline)
            HomeBillRow(
                bill = bill,
                hidden = state.hidden,
                onClick = { callbacks.onOpenCreditWallet(bill.walletId) },
            )
        }
        HorizontalDivider(thickness = FintrackTheme.sizes.hairline, color = FintrackTheme.colors.surfaces.outline)
        HomeBillsFooter(state = state, callbacks = callbacks)
    }
}

// FR-146: the instalments cell and the loans cell at the foot of the Tagihan card
@Composable
private fun HomeBillsFooter(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        InstalmentsCell(
            state = state,
            onClick = callbacks.onOpenInstalments,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier =
                Modifier
                    .width(FintrackTheme.sizes.hairline)
                    .fillMaxHeight()
                    .background(FintrackTheme.colors.surfaces.outline),
        )
        HomeFooterCell(
            label = stringResource(R.string.home_bills_loans),
            onClick = callbacks.onOpenLoans,
            modifier = Modifier.weight(1f),
        ) {
            HomeLoansText(
                receivable = state.netWorth.piutang,
                payable = state.netWorth.hutang,
                hidden = state.hidden,
            )
        }
    }
}

// FR-146: the instalments due this month, masked while amounts are hidden
@Composable
private fun InstalmentsCell(
    state: HomeUiState.Success,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    HomeFooterCell(
        label = stringResource(R.string.home_bills_instalments),
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(
            text =
                if (state.hidden) {
                    stringResource(R.string.home_amount_hidden)
                } else {
                    MoneyFormatter.format(state.bills.instalmentsDue)
                },
            style = FintrackTheme.typography.numberSmall,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// DOC-08: one footer cell of the Tagihan card: a caption over its value
@Composable
private fun HomeFooterCell(
    label: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit,
) {
    Column(
        modifier =
            modifier
                .then(if (onClick == null) Modifier else Modifier.clickable(role = Role.Button, onClick = onClick))
                .padding(horizontal = FintrackTheme.spacing.large, vertical = FintrackTheme.spacing.medium),
    ) {
        Text(
            text = label,
            style = FintrackTheme.typography.caption,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Box(modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall)) { value() }
    }
}

// DOC-08: "Pinjaman" value: receivable in the income colour, a faint slash and the payable in the expense colour
@Composable
private fun HomeLoansText(
    receivable: Money,
    payable: Money,
    hidden: Boolean,
) {
    val colors = FintrackTheme.colors
    if (hidden) {
        Text(
            text = stringResource(R.string.home_amount_hidden),
            style = FintrackTheme.typography.numberCompact,
            color = colors.texts.onSurface,
        )
        return
    }
    Row {
        Text(
            text = "+" + MoneyFormatter.formatCompact(receivable),
            style = FintrackTheme.typography.numberCompact,
            color = colors.semantics.income,
        )
        Text(
            text = " / ",
            style = FintrackTheme.typography.numberCompact.copy(fontWeight = FontWeight.Bold),
            color = colors.texts.faint,
        )
        Text(
            text = "−" + MoneyFormatter.formatCompact(payable),
            style = FintrackTheme.typography.numberCompact,
            color = colors.semantics.expense,
        )
    }
}
