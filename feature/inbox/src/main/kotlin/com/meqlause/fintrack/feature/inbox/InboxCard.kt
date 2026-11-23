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
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
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
    actions: InboxItemActions,
    onPick: (InboxPicker) -> Unit,
    modifier: Modifier = Modifier,
) {
    val swipe = rememberSwipeToDismissBoxState()
    SwipeEffect(swipe = swipe, itemId = card.itemId, actions = actions)
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
            CardBody(card, actions, onPick)
        }
    }
}

// FR-087: a completed swipe runs Catat or Abaikan, then the card returns to its resting place
@Composable
private fun SwipeEffect(
    swipe: SwipeToDismissBoxState,
    itemId: Long,
    actions: InboxItemActions,
) {
    LaunchedEffect(swipe.currentValue) {
        when (swipe.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> actions.onConfirm(itemId)
            SwipeToDismissBoxValue.EndToStart -> actions.onDismiss(itemId)
            SwipeToDismissBoxValue.Settled -> Unit
        }
        if (swipe.currentValue != SwipeToDismissBoxValue.Settled) swipe.snapTo(SwipeToDismissBoxValue.Settled)
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
    actions: InboxItemActions,
    onPick: (InboxPicker) -> Unit,
) {
    Column(modifier = Modifier.padding(FintrackScreenSizes.rowPaddingHorizontal)) {
        InboxHeaderRow(
            source = card.text.sourceLabel,
            title = card.text.merchant.ifEmpty { card.text.sourceLabel },
            subtitle = "${card.text.sourceLabel} · ${card.text.timeLabel}",
            amount = { InboxAmount(money = card.amount, type = card.type) },
        )
        CardActions(card, actions, onPick)
    }
}

// FR-087: the category and wallet chips, then the dismiss and the confirm buttons
@Composable
private fun CardActions(
    card: InboxCardUi,
    actions: InboxItemActions,
    onPick: (InboxPicker) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScreenPill(
            label = card.category.name ?: stringResource(R.string.inbox_pick_category),
            onClick = { onPick(InboxPicker(card.itemId, PickKind.CATEGORY)) },
            dotColor = if (card.category.name != null) FintrackTheme.colors.texts.faint else null,
        )
        ScreenPill(
            label = card.wallet.name ?: stringResource(R.string.inbox_pick_wallet),
            onClick = { onPick(InboxPicker(card.itemId, PickKind.WALLET)) },
        )
        Spacer(modifier = Modifier.weight(1f))
        CardButtons(card, actions)
    }
}

// FR-087: Abaikan is the outlined cross, Catat the filled check that needs a wallet and a category
@Composable
private fun CardButtons(
    card: InboxCardUi,
    actions: InboxItemActions,
) {
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
        enabled = card.wallet.id != null && card.category.id != null,
    )
}

@Composable
private fun RowScope.SwipeBackground(direction: SwipeToDismissBoxValue) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .clip(FintrackTheme.shapes.medium)
                .background(swipeColor(direction))
                .padding(horizontal = FintrackTheme.spacing.large),
        contentAlignment = swipeAlignment(direction),
    ) {
        Text(
            text = swipeLabel(direction),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

@Composable
private fun swipeLabel(direction: SwipeToDismissBoxValue): String =
    when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> stringResource(R.string.inbox_swipe_confirm)
        SwipeToDismissBoxValue.EndToStart -> stringResource(R.string.inbox_swipe_dismiss)
        SwipeToDismissBoxValue.Settled -> ""
    }

@Composable
private fun swipeColor(direction: SwipeToDismissBoxValue): Color =
    when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> FintrackTheme.colors.brands.primary
        SwipeToDismissBoxValue.EndToStart -> FintrackTheme.colors.surfaces.surfaceVariant
        SwipeToDismissBoxValue.Settled -> FintrackTheme.colors.surfaces.background
    }

private fun swipeAlignment(direction: SwipeToDismissBoxValue): Alignment =
    when (direction) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        SwipeToDismissBoxValue.Settled -> Alignment.Center
    }
