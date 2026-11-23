package com.meqlause.fintrack.core.ui.money

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackColors
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.UiPreviewCanvas
import com.meqlause.fintrack.core.ui.format.AmountSign
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// NFR-010: the sign is spoken ("minus Rp25.000") instead of read as a symbol
@Composable
fun MoneyText(
    money: Money,
    type: TransactionType,
    modifier: Modifier = Modifier,
    style: TextStyle = FintrackTheme.typography.numberMedium,
) {
    val sign = signOf(type)
    val spoken = spokenAmount(sign, MoneyFormatter.format(money))
    Text(
        text = MoneyFormatter.formatSigned(money, sign),
        modifier = modifier.semantics { contentDescription = spoken },
        style = style,
        color = colorOf(type, FintrackTheme.colors),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun spokenAmount(
    sign: AmountSign,
    amount: String,
): String =
    when (sign) {
        AmountSign.NONE -> amount
        AmountSign.MINUS -> stringResource(R.string.money_spoken_minus, amount)
        AmountSign.PLUS -> stringResource(R.string.money_spoken_plus, amount)
    }

// DOC-08: expense and lent or repaid money show −; income and borrowed or collected money show +
private fun signOf(type: TransactionType): AmountSign =
    when (type) {
        TransactionType.EXPENSE, TransactionType.LEND, TransactionType.REPAY -> AmountSign.MINUS
        TransactionType.INCOME, TransactionType.BORROW, TransactionType.COLLECT -> AmountSign.PLUS
        TransactionType.TRANSFER -> AmountSign.NONE
    }

private fun colorOf(
    type: TransactionType,
    colors: FintrackColors,
): Color =
    when (type) {
        TransactionType.EXPENSE -> {
            colors.semantics.expense
        }

        TransactionType.INCOME -> {
            colors.semantics.income
        }

        TransactionType.TRANSFER -> {
            colors.semantics.transfer
        }

        TransactionType.LEND, TransactionType.BORROW, TransactionType.COLLECT, TransactionType.REPAY -> {
            colors.texts.onSurface
        }
    }

@Composable
internal fun MoneyTextSample() {
    Column {
        MoneyText(money = Money(rupiah = 25_000L), type = TransactionType.EXPENSE)
        MoneyText(money = Money(rupiah = 5_000_000L), type = TransactionType.INCOME)
        MoneyText(money = Money(rupiah = 150_000L), type = TransactionType.TRANSFER)
        MoneyText(money = Money(rupiah = 100_000L), type = TransactionType.LEND)
    }
}

@Preview(name = "MoneyText light", showBackground = true)
@Composable
internal fun MoneyTextLightPreview() {
    UiPreviewCanvas(darkTheme = false) { MoneyTextSample() }
}

@Preview(name = "MoneyText dark", showBackground = true)
@Composable
internal fun MoneyTextDarkPreview() {
    UiPreviewCanvas(darkTheme = true) { MoneyTextSample() }
}
