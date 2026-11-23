package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.money.MoneyText

// FR-087, S-07: one pending item; swipe right = Catat, swipe left = Abaikan
@Composable
internal fun InboxCard(
    card: InboxCardUi,
    actions: InboxActions,
    onPickCategory: () -> Unit,
    onPickWallet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val swipe = rememberSwipeToDismissBoxState()
    LaunchedEffect(swipe.currentValue) {
        when (swipe.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> actions.onConfirm(card.itemId)
            SwipeToDismissBoxValue.EndToStart -> actions.onDismiss(card.itemId)
            SwipeToDismissBoxValue.Settled -> Unit
        }
        if (swipe.currentValue != SwipeToDismissBoxValue.Settled) swipe.snapTo(SwipeToDismissBoxValue.Settled)
    }
    SwipeToDismissBox(
        state = swipe,
        modifier = modifier,
        backgroundContent = { SwipeBackground(swipe.dismissDirection) },
    ) {
        FintrackCard(modifier = Modifier.fillMaxWidth()) {
            CardBody(card, actions, onPickCategory, onPickWallet)
        }
    }
}

@Composable
private fun CardBody(
    card: InboxCardUi,
    actions: InboxActions,
    onPickCategory: () -> Unit,
    onPickWallet: () -> Unit,
) {
    val spacing = FintrackTheme.spacing
    Column(
        modifier = Modifier.padding(spacing.large),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "${card.sourceLabel} · ${card.timeLabel}",
                style = FintrackTheme.typography.labelMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
            MoneyText(money = card.amount, type = card.type)
        }
        Text(
            text = card.merchant.ifEmpty { card.sourceLabel },
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        ChipRow(card, onPickCategory, onPickWallet)
        ButtonRow(card, actions)
    }
}

@Composable
private fun ChipRow(
    card: InboxCardUi,
    onPickCategory: () -> Unit,
    onPickWallet: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FintrackChip(
            label = card.categoryName ?: stringResource(R.string.inbox_pick_category),
            selected = card.categoryName != null,
            onClick = onPickCategory,
        )
        FintrackChip(
            label = card.walletName ?: stringResource(R.string.inbox_pick_wallet),
            selected = card.walletName != null,
            onClick = onPickWallet,
        )
    }
}

@Composable
private fun ButtonRow(
    card: InboxCardUi,
    actions: InboxActions,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = { actions.onDismiss(card.itemId) }) {
            Text(text = stringResource(R.string.inbox_dismiss))
        }
        FintrackButton(
            text = stringResource(R.string.inbox_confirm),
            onClick = { actions.onConfirm(card.itemId) },
            modifier = Modifier.weight(1f),
            enabled = card.walletId != null && card.categoryId != null,
        )
    }
}

@Composable
private fun RowScope.SwipeBackground(direction: SwipeToDismissBoxValue) {
    val colors = FintrackTheme.colors
    val (label, color, align) =
        when (direction) {
            SwipeToDismissBoxValue.StartToEnd -> {
                Triple(stringResource(R.string.inbox_swipe_confirm), colors.brands.primary, Alignment.CenterStart)
            }

            SwipeToDismissBoxValue.EndToStart -> {
                Triple(
                    stringResource(R.string.inbox_swipe_dismiss),
                    colors.surfaces.surfaceVariant,
                    Alignment.CenterEnd,
                )
            }

            SwipeToDismissBoxValue.Settled -> {
                Triple("", colors.surfaces.background, Alignment.Center)
            }
        }
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(FintrackTheme.shapes.medium)
                .background(color)
                .padding(horizontal = FintrackTheme.spacing.large),
        contentAlignment = align,
    ) {
        Text(text = label, style = FintrackTheme.typography.labelLarge, color = colors.texts.onSurface)
    }
}
