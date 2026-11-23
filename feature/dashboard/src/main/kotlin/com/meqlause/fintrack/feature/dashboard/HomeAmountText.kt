package com.meqlause.fintrack.feature.dashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// FR-040: an amount in the given style, or the privacy mask while amounts are hidden
@Composable
internal fun HomeAmountText(
    money: Money,
    hidden: Boolean,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    Text(
        text = if (hidden) stringResource(R.string.home_amount_hidden) else MoneyFormatter.format(money),
        modifier = modifier,
        style = style,
        color = FintrackTheme.colors.texts.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
