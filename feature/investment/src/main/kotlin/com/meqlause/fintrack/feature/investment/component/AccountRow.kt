package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentAccount
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentDates
import com.meqlause.fintrack.feature.investment.InvestmentLabels
import com.meqlause.fintrack.feature.investment.R

// FR-174, S-08: one investment account row, name over "Reksa dana · update 20 Nov", value over the gain percent
@Composable
internal fun AccountRow(
    account: InvestmentAccount,
    showDivider: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val kind = account.wallet.kind.investKind ?: InvestKind.OTHER
    val appearance = account.wallet.profile.appearance
    val kindLabel = stringResource(InvestmentLabels.kindRes(kind))
    val updated = account.valuation.lastValuedAt
    ScreenRow(
        modifier = modifier,
        showDivider = showDivider,
        onClick = onClick,
        leading = { IconCircle(iconKey = appearance.iconKey, colorKey = appearance.colorKey) },
        trailing = { AccountValue(account = account) },
    ) {
        Text(
            text = account.wallet.profile.name,
            style = FintrackScreenText.rowTitle(),
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text =
                if (updated == null) {
                    stringResource(R.string.investment_account_never, kindLabel)
                } else {
                    stringResource(R.string.investment_account_sub, kindLabel, InvestmentDates.dayMonth(updated))
                },
            style = FintrackScreenText.rowSub(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AccountValue(account: InvestmentAccount) {
    Column(horizontalAlignment = Alignment.End) {
        Text(
            text = MoneyFormatter.format(account.valuation.currentValue),
            style = FintrackScreenText.rowAmount(),
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
        )
        PercentText(valuation = account.valuation)
    }
}
