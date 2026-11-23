package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.InvestmentAccount
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentLabels
import com.meqlause.fintrack.feature.investment.R

// FR-174, S-08: one investment account in the Investasi list; a tap opens S-20
@Composable
internal fun AccountRow(
    account: InvestmentAccount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val typography = FintrackTheme.typography
    val kind = account.wallet.kind.investKind ?: InvestKind.OTHER
    FintrackCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.wallet.profile.name,
                    style = typography.titleMedium,
                    color = colors.texts.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = stringResource(InvestmentLabels.kindRes(kind)),
                    style = typography.labelMedium,
                    color = colors.texts.onSurfaceVariant,
                )
            }
            Text(
                text = MoneyFormatter.format(account.valuation.currentValue),
                style = typography.numberMedium,
                color = colors.texts.onSurface,
                maxLines = 1,
            )
        }
        GainText(valuation = account.valuation, style = typography.bodyMedium)
    }
}
