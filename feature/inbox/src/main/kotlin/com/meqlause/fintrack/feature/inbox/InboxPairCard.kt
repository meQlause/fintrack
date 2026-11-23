package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenCircleButton
import com.meqlause.fintrack.core.designsystem.component.ScreenPill
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-091, S-07: a debit and a credit that look like one transfer, with Catat sebagai Transfer or Catat terpisah
@Composable
internal fun InboxPairCard(
    pair: InboxPairUi,
    actions: InboxActions,
    onPickFrom: () -> Unit,
    onPickTo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val from = pair.fromWalletName.orEmpty()
    val to = pair.toWalletName.orEmpty()
    ScreenCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(FintrackScreenSizes.rowPaddingHorizontal)) {
            InboxHeaderRow(
                source = from,
                title = stringResource(R.string.inbox_pair_title, from.ifEmpty { "?" }, to.ifEmpty { "?" }),
                subtitle = "$from \u2192 $to",
                amount = { InboxAmount(money = pair.amount, type = TransactionType.TRANSFER) },
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val transfer = FintrackTheme.colors.semantics.transfer
                ScreenPill(
                    label = pair.fromWalletName ?: stringResource(R.string.inbox_pick_wallet),
                    onClick = onPickFrom,
                    contentColor = transfer,
                )
                ScreenPill(
                    label = pair.toWalletName ?: stringResource(R.string.inbox_pick_wallet),
                    onClick = onPickTo,
                    contentColor = transfer,
                )
                Spacer(modifier = Modifier.weight(1f))
                PairButtons(pair, actions)
            }
        }
    }
}

@Composable
private fun PairButtons(
    pair: InboxPairUi,
    actions: InboxActions,
) {
    Text(
        text = stringResource(R.string.inbox_pair_split),
        style = FintrackScreenText.style(size = 12.5f, weight = 600),
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier =
            Modifier
                .height(FintrackScreenSizes.circleButton)
                .clip(CircleShape)
                .border(FintrackTheme.sizes.hairline, FintrackTheme.colors.surfaces.outline, CircleShape)
                .clickable { actions.onSplitPair(pair.fromItemId) }
                .padding(horizontal = FintrackTheme.spacing.medium)
                .wrapContentHeight(Alignment.CenterVertically),
    )
    ScreenCircleButton(
        iconRes = DesignR.drawable.ic_check,
        description = stringResource(R.string.inbox_pair_confirm),
        onClick = { actions.onConfirmPair(pair.fromItemId) },
        filled = true,
        enabled = pair.fromWalletId != null && pair.toWalletId != null,
    )
}
