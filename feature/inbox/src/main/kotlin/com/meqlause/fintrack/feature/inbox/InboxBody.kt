package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.money.MoneyText

// S-07: the body under the header; the states are Loading (blank), AccessOff, Empty and Success
@Composable
internal fun InboxBody(
    state: InboxUiState,
    actions: InboxActions,
    infoShown: Boolean,
    onInfoDismiss: () -> Unit,
    resolvedOpen: Boolean,
    onToggleResolved: () -> Unit,
    onPick: (InboxPicker) -> Unit,
) {
    when {
        !state.isLoaded -> {
            Unit
        }

        !state.hasAccess -> {
            AccessOff(actions)
        }

        state.cards.isEmpty() && state.pairs.isEmpty() -> {
            EmptyBody()
        }

        else -> {
            InboxList(state, actions, infoShown, onInfoDismiss, resolvedOpen, onToggleResolved, onPick)
        }
    }
}

@Composable
private fun AccessOff(actions: InboxActions) {
    EmptyState(
        copy =
            EmptyStateCopy(
                title = stringResource(R.string.inbox_access_title),
                body = stringResource(R.string.inbox_access_body),
            ),
        iconRes = R.drawable.ic_history,
        action = {
            FintrackButton(text = stringResource(R.string.inbox_access_action), onClick = actions.onEnableAccess)
        },
    )
}

@Composable
private fun EmptyBody() {
    EmptyState(
        copy =
            EmptyStateCopy(
                title = stringResource(R.string.inbox_empty_title),
                body = stringResource(R.string.inbox_empty_body),
            ),
        iconRes = R.drawable.ic_history,
    )
}

@Composable
private fun InboxList(
    state: InboxUiState,
    actions: InboxActions,
    infoShown: Boolean,
    onInfoDismiss: () -> Unit,
    resolvedOpen: Boolean,
    onToggleResolved: () -> Unit,
    onPick: (InboxPicker) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                horizontal = FintrackTheme.spacing.large,
                vertical = FintrackTheme.spacing.small,
            ),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        if (infoShown) {
            item(key = "info") { InfoCard(onDismiss = onInfoDismiss) }
        }
        pendingItems(state, actions, onPick)
        resolvedSection(state, resolvedOpen, onToggleResolved)
    }
}

// S-07: the pair cards first, then the single cards, both newest first
private fun LazyListScope.pendingItems(
    state: InboxUiState,
    actions: InboxActions,
    onPick: (InboxPicker) -> Unit,
) {
    items(state.pairs, key = { "pair-${it.fromItemId}" }) { pair ->
        InboxPairCard(
            pair = pair,
            actions = actions,
            onPickFrom = { onPick(InboxPicker(pair.fromItemId, PickKind.WALLET)) },
            onPickTo = { onPick(InboxPicker(pair.toItemId, PickKind.TARGET_WALLET)) },
        )
    }
    items(state.cards, key = { "card-${it.itemId}" }) { card ->
        InboxCard(
            card = card,
            actions = actions,
            onPickCategory = { onPick(InboxPicker(card.itemId, PickKind.CATEGORY)) },
            onPickWallet = { onPick(InboxPicker(card.itemId, PickKind.WALLET)) },
        )
    }
}

// S-07 "Selesai": collapsed by default; lists the confirmed and dismissed items of the last seven days
private fun LazyListScope.resolvedSection(
    state: InboxUiState,
    resolvedOpen: Boolean,
    onToggleResolved: () -> Unit,
) {
    if (state.resolved.isEmpty()) return
    item(key = "resolved-header") {
        TextButton(onClick = onToggleResolved) {
            Text(text = "${stringResource(R.string.inbox_resolved_title)} (${state.resolved.size})")
        }
    }
    if (resolvedOpen) {
        items(state.resolved, key = { "resolved-${it.itemId}" }) { row -> ResolvedRow(row) }
    }
}

@Composable
private fun InfoCard(onDismiss: () -> Unit) {
    FintrackCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(FintrackTheme.spacing.large)) {
            Text(
                text = stringResource(R.string.inbox_info_text),
                style = FintrackTheme.typography.bodyMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.inbox_info_dismiss))
            }
        }
    }
}

@Composable
private fun ResolvedRow(resolvedRow: InboxResolvedUi) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = FintrackTheme.spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = resolvedRow.label,
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        MoneyText(money = resolvedRow.amount, type = resolvedRow.type)
    }
}
