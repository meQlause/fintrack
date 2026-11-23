package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackAppBadges
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.format.AmountSign
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// S-07: the top line of a card, the 36 px app badge, the merchant over "BCA · 09:42" and the amount at the right
@Composable
internal fun InboxHeaderRow(
    source: String,
    title: String,
    subtitle: String,
    amount: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.rowGap),
    ) {
        Box(
            modifier =
                Modifier
                    .size(FintrackScreenSizes.appBadge)
                    .clip(RoundedCornerShape(FintrackScreenSizes.appBadgeCorner))
                    .background(FintrackAppBadges.colorOf(source)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = FintrackAppBadges.letterOf(source),
                style = FintrackScreenText.style(size = 12f, weight = 700),
                color = Color.White,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = FintrackScreenText.style(size = 14f, weight = 600),
                color = FintrackTheme.colors.texts.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = FintrackScreenText.rowSub(),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        amount()
    }
}

// S-07: the 15 px bold amount, red with a minus for spending, green with a plus for income, blue for transfers
@Composable
internal fun InboxAmount(
    money: Money,
    type: TransactionType,
) {
    val semantics = FintrackTheme.colors.semantics
    val (sign, color) =
        when (type) {
            TransactionType.EXPENSE, TransactionType.LEND, TransactionType.REPAY -> AmountSign.MINUS to semantics.expense
            TransactionType.INCOME, TransactionType.BORROW, TransactionType.COLLECT -> AmountSign.PLUS to semantics.income
            TransactionType.TRANSFER -> AmountSign.NONE to semantics.transfer
        }
    Text(
        text = MoneyFormatter.formatSigned(money, sign),
        style = FintrackScreenText.number(size = 15f, weight = 700),
        color = color,
        maxLines = 1,
    )
}
