package com.meqlause.fintrack.feature.investment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentPortfolio
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.investment.component.AccountRow
import com.meqlause.fintrack.feature.investment.component.ActivityRow
import com.meqlause.fintrack.feature.investment.component.PortfolioCard
import com.meqlause.fintrack.core.designsystem.R as DesignR

// S-08: the list area; while loading it is blank, without accounts it shows the empty state
@Composable
internal fun InvestmentBody(
    portfolio: InvestmentPortfolio?,
    onOpenAccount: (WalletId) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        portfolio == null -> Unit
        portfolio.accounts.isEmpty() -> EmptyBody(modifier = modifier)
        else -> PortfolioList(portfolio = portfolio, onOpenAccount = onOpenAccount, modifier = modifier)
    }
}

// S-08: "Belum ada investasi" with the add action below the screen
@Composable
private fun EmptyBody(modifier: Modifier) {
    EmptyState(
        copy =
            EmptyStateCopy(
                title = stringResource(R.string.investment_empty_title),
                body = stringResource(R.string.investment_empty_body),
            ),
        iconRes = DesignR.drawable.ic_trend,
        modifier = modifier,
    )
}

// S-08: total and allocation, then the accounts and the newest activity
@Composable
private fun PortfolioList(
    portfolio: InvestmentPortfolio,
    onOpenAccount: (WalletId) -> Unit,
    modifier: Modifier,
) {
    val names = portfolio.accounts.associate { it.wallet.walletId to it.wallet.profile.name }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        item { PortfolioCard(portfolio = portfolio) }
        item { SectionTitle(text = stringResource(R.string.investment_accounts_title)) }
        items(portfolio.accounts, key = { it.wallet.walletId.value }) { account ->
            AccountRow(account = account, onClick = { onOpenAccount(account.wallet.walletId) })
        }
        item { SectionTitle(text = stringResource(R.string.investment_activity_title)) }
        if (portfolio.recent.isEmpty()) {
            item { Text(text = stringResource(R.string.investment_activity_none)) }
        }
        items(portfolio.recent) { activity ->
            ActivityRow(activity = activity, subtitle = names[activity.walletId].orEmpty())
        }
    }
}

// S-08: a heading above one group of rows
@Composable
internal fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = FintrackTheme.typography.labelLarge,
        color = FintrackTheme.colors.texts.onSurface,
    )
}
