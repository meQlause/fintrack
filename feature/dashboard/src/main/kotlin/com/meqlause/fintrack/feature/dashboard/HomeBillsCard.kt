package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId

// FR-137, FR-146, S-03: the Tagihan card; a bill opens its credit wallet (S-14), the instalment cell opens S-16
@Composable
internal fun HomeBillsCard(
    bills: HomeBills,
    hidden: Boolean,
    onOpenCreditWallet: (WalletId) -> Unit,
    onOpenInstalments: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FintrackCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.home_bills_title),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        bills.bills.forEach { bill ->
            HomeBillRow(
                bill = bill,
                hidden = hidden,
                onClick = { onOpenCreditWallet(bill.walletId) },
                modifier = Modifier.padding(top = FintrackTheme.spacing.small),
            )
        }
        Box(
            modifier =
                Modifier
                    .padding(top = FintrackTheme.spacing.small)
                    .fillMaxWidth()
                    .height(FintrackTheme.sizes.hairline)
                    .background(FintrackTheme.colors.surfaces.outline),
        )
        HomeInstalmentCell(
            amount = bills.instalmentsDue,
            hidden = hidden,
            onClick = onOpenInstalments,
            modifier = Modifier.padding(top = FintrackTheme.spacing.small),
        )
    }
}

// FR-146, S-03: "Cicilan bulan ini" with the portions whose statements fall due in the current period
@Composable
private fun HomeInstalmentCell(
    amount: Money,
    hidden: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth().clickable(role = Role.Button, onClick = onClick)) {
        Text(
            text = stringResource(R.string.home_bills_instalments),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        HomeAmountText(money = amount, hidden = hidden, style = FintrackTheme.typography.bodyLarge)
    }
}
