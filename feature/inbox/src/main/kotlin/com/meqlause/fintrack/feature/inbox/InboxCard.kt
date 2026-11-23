package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenCircleButton
import com.meqlause.fintrack.core.designsystem.component.ScreenPill
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.R as DesignR

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
        modifier =
            modifier.swipeAlternatives(
                onConfirm = { actions.onConfirm(card.itemId) },
                onDismiss = { actions.onDismiss(card.itemId) },
            ),
        backgroundContent = { SwipeBackground(swipe.dismissDirection) },
    ) {
        ScreenCard(modifier = Modifier.fillMaxWidth()) {
            CardBody(card, actions, onPickCategory, onPickWallet)
        }
    }
}

// NFR-010: the swipe gestures have screen-reader alternatives under the card's custom actions
@Composable
private fun Modifier.swipeAlternatives(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
): Modifier {
    val confirmLabel = stringResource(R.string.inbox_confirm)
    val dismissLabel = stringResource(R.string.inbox_dismiss)
    return semantics {
        customActions =
            listOf(
                CustomAccessibilityAction(confirmLabel) {
                    onConfirm()
                    true
                },
                CustomAccessibilityAction(dismissLabel) {
                    onDismiss()
                    true
                },
            )
    }
}

@Composable
private fun CardBody(
    card: InboxCardUi,
    actions: InboxActions,
    onPickCategory: () -> Unit,
    onPickWallet: () -> Unit,
) {
    Column(modifier = Modifier.padding(FintrackScreenSizes.rowPaddingHorizontal)) {
        InboxHeaderRow(
            source = card.sourceLabel,
            title = card.merchant.ifEmpty { card.sourceLabel },
            subtitle = "${card.sourceLabel} \u00B7 ${card.timeLabel}",
            amount = { InboxAmount(money = card.amount, type = card.type) },
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ScreenPill(
                label = card.categoryName ?: stringResource(R.string.inbox_pick_category),
                onClick = onPickCategory,
                dotColor = if (card.categoryName != null) FintrackTheme.colors.texts.faint else null,
            )
            ScreenPill(label = card.walletName ?: stringResource(R.string.inbox_pick_wallet), onClick = onPickWallet)
            Spacer(modifier = Modifier.weight(1f))
            ScreenCircleButton(
                iconRes = DesignR.drawable.ic_close,
                description = stringResource(R.string.inbox_dismiss),
                onClick = { actions.onDismiss(card.itemId) },
            )
            ScreenCircleButton(
                iconRes = DesignR.drawable.ic_check,
                description = stringResource(R.string.inbox_confirm),
                onClick = { actions.onConfirm(card.itemId) },
                filled = true,
                enabled = card.walletId != null && card.categoryId != null,
            )
        }
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
