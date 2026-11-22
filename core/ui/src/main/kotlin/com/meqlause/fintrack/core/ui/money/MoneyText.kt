package com.meqlause.fintrack.core.ui.money

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.theme.FintrackColors
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.UiPreviewCanvas
import com.meqlause.fintrack.core.ui.format.AmountSign
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// DOC-08: an amount with its sign and the colour of its money direction; loan lines stay neutral
@Composable
fun MoneyText(
    money: Money,
    type: TransactionType,
    modifier: Modifier = Modifier,
) {
    Text(
        text = MoneyFormatter.formatSigned(money, signOf(type)),
        modifier = modifier,
        style = FintrackTheme.typography.numberMedium,
        color = colorOf(type, FintrackTheme.colors),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
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
