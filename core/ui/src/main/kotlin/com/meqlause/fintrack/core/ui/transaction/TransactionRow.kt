package com.meqlause.fintrack.core.ui.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.isLargeText
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.UiPreviewCanvas
import com.meqlause.fintrack.core.ui.money.MoneyText
import com.meqlause.fintrack.core.designsystem.R as DesignR

private const val LARGE_TEXT_LINES = 3

// DOC-08 / DOC-09: icon circle, title and wallet · time, then the signed amount; one tappable row in a list
@Composable
fun TransactionRow(
    params: TransactionRowParams,
    callbacks: TransactionRowCallbacks,
    modifier: Modifier = Modifier,
) {
    val body =
        modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = callbacks.onClick)
            .padding(horizontal = FintrackTheme.sizes.rowInset, vertical = FintrackTheme.spacing.medium)
    if (isLargeText()) {
        StackedTransaction(params = params, modifier = body)
    } else {
        Row(
            modifier = body,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
        ) {
            IconCircle(look = params.look)
            TransactionTitles(params = params, modifier = Modifier.weight(1f))
            TransactionAmount(params = params)
        }
    }
}

// NFR-010: at large font sizes the amount sits under the titles, so neither is cut
@Composable
private fun StackedTransaction(
    params: TransactionRowParams,
    modifier: Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
        ) {
            IconCircle(look = params.look)
            TransactionTitles(params = params, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TransactionAmount(params = params)
        }
    }
}

@Composable
private fun TransactionAmount(params: TransactionRowParams) {
    MoneyText(
        money = params.amount,
        type = params.type,
        style = FintrackTheme.typography.numberRow,
    )
}

@Composable
private fun TransactionTitles(
    params: TransactionRowParams,
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = params.title,
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = if (isLargeText()) LARGE_TEXT_LINES else 1,
            overflow = TextOverflow.Ellipsis,
        )
        TransactionSubtitle(params = params)
    }
}

@Composable
private fun TransactionSubtitle(params: TransactionRowParams) {
    Row(
        modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        if (params.auto) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_spark),
                contentDescription = stringResource(R.string.row_auto_description),
                tint = FintrackTheme.colors.brands.primary,
                modifier = Modifier.size(FintrackTheme.sizes.tinyGlyph),
            )
        }
        Text(
            text = params.subtitle,
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = if (isLargeText()) LARGE_TEXT_LINES else 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun TransactionRowSample() {
    TransactionRow(
        params =
            TransactionRowParams(
                look = IconLook(iconKey = "food", colorKey = "orange"),
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
                look = IconLook(iconKey = "salary", colorKey = "green"),
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
