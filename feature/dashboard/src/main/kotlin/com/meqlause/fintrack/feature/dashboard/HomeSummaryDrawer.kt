package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money

// FR-040, FR-041, S-03: the Ringkasan drawer; Kekayaan bersih on top (BR-053), then the period, the parts and the wallets action
@Composable
internal fun HomeSummaryDrawer(
    state: HomeUiState.Success,
    onClose: () -> Unit,
    onOpenWallets: () -> Unit,
) {
    val worth = state.netWorth
    PickerDrawer(title = stringResource(R.string.home_summary_title), onClose = onClose) {
        SummaryLine(
            label = stringResource(R.string.home_summary_net_worth),
            money = worth.total,
            hidden = state.hidden,
        )
        SummaryLine(
            label = stringResource(R.string.home_summary_income),
            money = state.period.income,
            hidden = state.hidden,
        )
        SummaryLine(
            label = stringResource(R.string.home_summary_expense),
            money = state.period.expense,
            hidden = state.hidden,
        )
        SummaryLine(
            label = stringResource(R.string.home_summary_saldo),
            money = worth.saldo,
            hidden = state.hidden,
        )
        SummaryLine(
            label = stringResource(R.string.home_summary_investment),
            money = worth.investasi,
            hidden = state.hidden,
        )
        SummaryLine(
            label = stringResource(R.string.home_summary_receivable),
            money = worth.piutang,
            hidden = state.hidden,
        )
        SummaryLine(
            label = stringResource(R.string.home_summary_card_debt),
            money = worth.utangKartu,
            hidden = state.hidden,
        )
        SummaryLine(
            label = stringResource(R.string.home_summary_debt),
            money = worth.hutang,
            hidden = state.hidden,
        )
        FintrackButton(
            text = stringResource(R.string.home_summary_action),
            onClick = onOpenWallets,
            modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        )
    }
}

// S-03: one label with its amount on the same line
@Composable
private fun SummaryLine(
    label: String,
    money: Money,
    hidden: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = FintrackTheme.typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        HomeAmountText(money = money, hidden = hidden, style = FintrackTheme.typography.bodyLarge)
    }
}
