package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.RowSlots
import com.meqlause.fintrack.core.designsystem.component.RowStyle
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.investment.ActivityKind
import com.meqlause.fintrack.core.domain.investment.InvestmentActivity
import com.meqlause.fintrack.core.ui.format.AmountSign
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.investment.InvestmentDates
import com.meqlause.fintrack.feature.investment.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-173, S-08: one timeline entry, "Update nilai · Bibit" over its date, with the amount at the right
@Composable
internal fun ActivityRow(
    activity: InvestmentActivity,
    accountName: String?,
    showDivider: Boolean,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(titleRes(activity.kind))
    ScreenRow(
        modifier = modifier,
        style = activityRowStyle(showDivider),
        slots = RowSlots(leading = { ActivityMark(kind = activity.kind) }, trailing = { ActivityAmount(activity) }),
    ) {
        Text(
            text = if (accountName == null) title else "$title · $accountName",
            style = FintrackScreenText.style(size = 13.5f, weight = 600),
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = InvestmentDates.dayMonth(activity.occurredAt),
            style = FintrackScreenText.rowSub(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

// FR-173: the timeline row is a little tighter than the standard one
@Composable
private fun activityRowStyle(showDivider: Boolean): RowStyle =
    RowStyle(
        showDivider = showDivider,
        contentPadding =
            PaddingValues(
                horizontal = FintrackScreenSizes.rowPaddingHorizontal,
                vertical = FintrackScreenSizes.rowPaddingVertical - FintrackTheme.spacing.xxSmall / 2,
            ),
    )

// FR-173: the amount with its sign; a valuation is shown in the primary colour
@Composable
private fun ActivityAmount(activity: InvestmentActivity) {
    val isValuation = activity.kind == ActivityKind.VALUATION
    Text(
        text = MoneyFormatter.formatSigned(activity.amount, signOf(activity.kind)),
        style = FintrackScreenText.number(size = 13.5f, weight = 600),
        color = if (isValuation) FintrackTheme.colors.brands.primary else FintrackTheme.colors.texts.onSurface,
        maxLines = 1,
    )
}

// S-08: the 30 px grey square with a trend glyph for a valuation and a plus or minus for money moved
@Composable
private fun ActivityMark(kind: ActivityKind) {
    val isValuation = kind == ActivityKind.VALUATION
    Box(
        modifier =
            Modifier
                .size(FintrackScreenSizes.smallIconBox)
                .clip(RoundedCornerShape(FintrackScreenSizes.smallIconCorner))
                .background(FintrackTheme.colors.surfaces.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(if (isValuation) DesignR.drawable.ic_trend else DesignR.drawable.ic_plus),
            contentDescription = null,
            tint = if (isValuation) FintrackTheme.colors.brands.primary else FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.size(FintrackScreenSizes.smallGlyph),
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
