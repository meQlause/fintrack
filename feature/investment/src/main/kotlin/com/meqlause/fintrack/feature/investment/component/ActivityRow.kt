package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.ActivityKind
import com.meqlause.fintrack.core.domain.investment.InvestmentActivity
import com.meqlause.fintrack.core.ui.format.AmountSign
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentDates
import com.meqlause.fintrack.feature.investment.R

// FR-173: one entry of the investment timeline; subtitle names the account when the list covers several
@Composable
internal fun ActivityRow(
    activity: InvestmentActivity,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val typography = FintrackTheme.typography
    val date = InvestmentDates.shortDate(activity.occurredAt)
    val detail = if (subtitle.isBlank()) date else "$date · $subtitle"
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = FintrackTheme.spacing.xSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ActivityText(title = stringResource(titleRes(activity.kind)), detail = detail)
        Text(
            text = MoneyFormatter.formatSigned(activity.amount, signOf(activity.kind)),
            style = typography.bodyLarge,
            color = colors.texts.onSurface,
            maxLines = 1,
        )
    }
}

// FR-173: the heading of an entry with its date and account line underneath
@Composable
private fun RowScope.ActivityText(
    title: String,
    detail: String,
) {
    val colors = FintrackTheme.colors
    val typography = FintrackTheme.typography
    Column(modifier = Modifier.weight(1f)) {
        Text(text = title, style = typography.bodyLarge, color = colors.texts.onSurface)
        Text(
            text = detail,
            style = typography.labelMedium,
            color = colors.texts.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// FR-173: the heading of each kind of timeline entry
private fun titleRes(kind: ActivityKind): Int =
    when (kind) {
        ActivityKind.PUT_IN -> R.string.investment_put_in
        ActivityKind.TAKE_OUT -> R.string.investment_take_out
        ActivityKind.VALUATION -> R.string.investment_valued
    }

// FR-173: money in is shown with a plus, money out with a minus, a valuation without a sign
private fun signOf(kind: ActivityKind): AmountSign =
    when (kind) {
        ActivityKind.PUT_IN -> AmountSign.PLUS
        ActivityKind.TAKE_OUT -> AmountSign.MINUS
        ActivityKind.VALUATION -> AmountSign.NONE
    }
