package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.money.MoneyText

// FR-091, S-07: a debit and a credit that look like one transfer, with Catat sebagai Transfer or Catat terpisah
@Composable
internal fun InboxPairCard(
    pair: InboxPairUi,
    actions: InboxActions,
    onPickFrom: () -> Unit,
    onPickTo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FintrackCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(FintrackTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            PairHeader(pair)
            Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
                FintrackChip(
                    label = pair.fromWalletName ?: stringResource(R.string.inbox_pick_wallet),
                    selected = pair.fromWalletName != null,
                    onClick = onPickFrom,
                )
                FintrackChip(
                    label = pair.toWalletName ?: stringResource(R.string.inbox_pick_wallet),
                    selected = pair.toWalletName != null,
                    onClick = onPickTo,
                )
            }
            PairButtons(pair, actions)
        }
    }
}

@Composable
private fun PairHeader(pair: InboxPairUi) {
    val title = stringResource(R.string.inbox_pair_title, pair.fromWalletName ?: "?", pair.toWalletName ?: "?")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = title,
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(end = FintrackTheme.spacing.small),
        )
        MoneyText(money = pair.amount, type = TransactionType.TRANSFER)
    }
}

@Composable
private fun PairButtons(
    pair: InboxPairUi,
    actions: InboxActions,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = { actions.onSplitPair(pair.fromItemId) }) {
            Text(text = stringResource(R.string.inbox_pair_split))
        }
        FintrackButton(
            text = stringResource(R.string.inbox_pair_confirm),
            onClick = { actions.onConfirmPair(pair.fromItemId) },
            modifier = Modifier.weight(1f),
            enabled = pair.fromWalletId != null && pair.toWalletId != null,
        )
    }
}
