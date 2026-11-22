package com.meqlause.fintrack.core.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.UiPreviewCanvas
import com.meqlause.fintrack.core.ui.money.MoneyText
import com.meqlause.fintrack.core.designsystem.R as DesignR

// DOC-08 / DOC-09: icon circle, title and wallet · time, then the signed amount; one tappable row in a list
@Composable
fun TransactionRow(
    params: TransactionRowParams,
    callbacks: TransactionRowCallbacks,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = FintrackTheme.sizes.formRowHeight)
                .clickable(role = Role.Button, onClick = callbacks.onClick)
                .padding(vertical = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        TransactionIconCircle(iconRes = params.iconRes, tint = params.iconColor)
        TransactionTitles(title = params.title, subtitle = params.subtitle, modifier = Modifier.weight(1f))
        MoneyText(money = params.amount, type = params.type)
    }
}

@Composable
private fun TransactionTitles(
    title: String,
    subtitle: String,
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtitle,
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TransactionIconCircle(
    iconRes: Int,
    tint: Color,
) {
    Box(
        modifier =
            Modifier
                .size(FintrackTheme.sizes.iconLarge)
                .clip(FintrackTheme.shapes.full)
                .background(tint.copy(alpha = FintrackTheme.alphas.categoryTint)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(FintrackTheme.sizes.iconMedium),
        )
    }
}

@Composable
internal fun TransactionRowSample() {
    val colors = FintrackTheme.colors
    TransactionRow(
        params =
            TransactionRowParams(
                iconRes = DesignR.drawable.ic_wallet,
                iconColor = colors.brands.primary,
                title = stringResource(R.string.sample_row_expense_title),
                subtitle = stringResource(R.string.sample_row_expense_subtitle),
                amount = Money(rupiah = 25_000L),
                type = TransactionType.EXPENSE,
            ),
        callbacks = TransactionRowCallbacks(onClick = {}),
    )
    TransactionRow(
        params =
            TransactionRowParams(
                iconRes = DesignR.drawable.ic_wallet,
                iconColor = colors.semantics.income,
                title = stringResource(R.string.sample_row_income_title),
                subtitle = stringResource(R.string.sample_row_income_subtitle),
                amount = Money(rupiah = 5_000_000L),
                type = TransactionType.INCOME,
            ),
        callbacks = TransactionRowCallbacks(onClick = {}),
    )
}

@Preview(name = "TransactionRow light", showBackground = true)
@Composable
internal fun TransactionRowLightPreview() {
    UiPreviewCanvas(darkTheme = false) { TransactionRowSample() }
}

@Preview(name = "TransactionRow dark", showBackground = true)
@Composable
internal fun TransactionRowDarkPreview() {
    UiPreviewCanvas(darkTheme = true) { TransactionRowSample() }
}
