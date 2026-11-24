package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.RowSlots
import com.meqlause.fintrack.core.designsystem.component.RowStyle
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.core.designsystem.R as DesignR

// S-07: the body under the header; the states are Loading (blank), AccessOff, Empty and Success
@Composable
internal fun InboxBody(
    state: InboxUiState,
    actions: InboxActions,
    panels: InboxPanels,
) {
    when {
        !state.isLoaded -> Unit
        !state.hasAccess -> AccessOff(actions)
        state.pending.isEmpty -> EmptyBody()
        else -> InboxList(state, actions, panels)
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
    panels: InboxPanels,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                horizontal = FintrackScreenSizes.screenGutter,
                vertical = FintrackTheme.spacing.xSmall,
            ),
        verticalArrangement =
            Arrangement.spacedBy(
                FintrackScreenSizes.pillGap + FintrackTheme.spacing.xSmall - FintrackTheme.spacing.xxSmall,
            ),
    ) {
        if (panels.infoShown) {
            item(key = "info") { InfoCard(onDismiss = panels.onInfoDismiss) }
        }
        pendingItems(state.pending, actions.items, panels.onPick)
        resolvedSection(state.resolved, panels)
    }
}

// S-07: pair cards, then single cards, newest first; a dismissed card fades out and the rest close up
private fun LazyListScope.pendingItems(
    pending: InboxPending,
    actions: InboxItemActions,
    onPick: (InboxPicker) -> Unit,
) {
    items(pending.pairs, key = { "pair-${it.fromItemId}" }) { pair ->
        InboxPairCard(pair = pair, actions = actions, onPick = onPick, modifier = Modifier.animateItem())
    }
    items(pending.cards, key = { "card-${it.itemId}" }) { card ->
        InboxCard(card = card, actions = actions, onPick = onPick, modifier = Modifier.animateItem())
    }
}

// S-07 "Selesai": collapsed by default; lists the confirmed and dismissed items of the last seven days
private fun LazyListScope.resolvedSection(
    resolved: List<InboxResolvedUi>,
    panels: InboxPanels,
) {
    if (resolved.isEmpty()) return
    item(key = "resolved-header") {
        Text(
            text = "${stringResource(R.string.inbox_resolved_title)} (${resolved.size})",
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier =
                Modifier
                    .padding(top = FintrackTheme.spacing.large, start = FintrackTheme.spacing.xxSmall)
                    .clickable(onClick = panels.onToggleResolved),
        )
    }
    if (panels.resolvedOpen) {
        item(key = "resolved-card") {
            ScreenCard {
                resolved.forEachIndexed { index, row -> ResolvedRow(row, showDivider = index > 0) }
            }
        }
    }
}

@Composable
private fun InfoCard(onDismiss: () -> Unit) {
    ScreenCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(FintrackScreenSizes.rowPaddingHorizontal)) {
            Text(
                text = stringResource(R.string.inbox_info_text),
                style = FintrackScreenText.style(size = 13f, weight = 400),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.inbox_info_dismiss), color = FintrackTheme.colors.brands.primary)
            }
        }
    }
}

// S-07: one settled item, a muted line with its amount and a check or a cross at the right
@Composable
private fun ResolvedRow(
    resolvedRow: InboxResolvedUi,
    showDivider: Boolean,
) {
    ScreenRow(
        style =
            RowStyle(
                showDivider = showDivider,
                contentPadding =
                    PaddingValues(
                        horizontal = FintrackScreenSizes.rowPaddingHorizontal,
                        vertical = FintrackTheme.spacing.small + FintrackTheme.spacing.xxSmall,
                    ),
            ),
        slots = RowSlots(leading = {}, trailing = { ResolvedIcon(isConfirmed = resolvedRow.isConfirmed) }),
    ) {
        Text(
            text = resolvedRow.label + " · " + MoneyFormatter.format(resolvedRow.amount),
            style = FintrackScreenText.style(size = 13f, weight = 400),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// S-07: the check for a confirmed item and the cross for a dismissed one
@Composable
private fun ResolvedIcon(isConfirmed: Boolean) {
    Icon(
        painter = painterResource(if (isConfirmed) DesignR.drawable.ic_check else DesignR.drawable.ic_close),
        contentDescription = null,
        tint =
            if (isConfirmed) {
                FintrackTheme.colors.brands.primary
            } else {
                FintrackTheme.colors.texts.onSurfaceVariant
            },
        modifier = Modifier.size(FintrackScreenSizes.circleGlyph),
    )
}
