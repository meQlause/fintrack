package com.meqlause.fintrack.feature.investment

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.component.ScreenGroup
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.domain.investment.InvestmentPortfolio
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.investment.component.AccountRow
import com.meqlause.fintrack.feature.investment.component.ActivityRow
import com.meqlause.fintrack.feature.investment.component.AllocationCard
import com.meqlause.fintrack.feature.investment.component.PortfolioHero
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
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = FintrackScreenSizes.screenBottom),
    ) {
        item { PortfolioHero(portfolio = portfolio) }
        item {
            AllocationCard(
                portfolio = portfolio,
                modifier = Modifier.padding(top = FintrackScreenSizes.screenTop),
            )
        }
        item { AccountsGroup(portfolio = portfolio, onOpenAccount = onOpenAccount) }
        item { RecentGroup(portfolio = portfolio) }
    }
}

// S-08: every account of the portfolio, each one opens its detail
@Composable
private fun AccountsGroup(
    portfolio: InvestmentPortfolio,
    onOpenAccount: (WalletId) -> Unit,
) {
    ScreenGroup(label = stringResource(R.string.investment_accounts_title)) {
        portfolio.accounts.forEachIndexed { index, account ->
            AccountRow(
                account = account,
                showDivider = index > 0,
                onClick = { onOpenAccount(account.wallet.walletId) },
            )
        }
    }
}

// S-08: the newest activity of the portfolio, named with the account it belongs to
@Composable
private fun RecentGroup(portfolio: InvestmentPortfolio) {
    val names = portfolio.accounts.associate { it.wallet.walletId to it.wallet.profile.name }
    ScreenGroup(label = stringResource(R.string.investment_activity_title)) {
        portfolio.recent.forEachIndexed { index, activity ->
            ActivityRow(
                activity = activity,
                accountName = names[activity.walletId].orEmpty(),
                showDivider = index > 0,
            )
        }
    }
}
