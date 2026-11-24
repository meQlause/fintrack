package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.meqlause.fintrack.core.designsystem.component.CircleIcon
import com.meqlause.fintrack.core.designsystem.component.PillLook
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenCircleButton
import com.meqlause.fintrack.core.designsystem.component.ScreenPill
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.isLargeText
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-091, S-07: a debit and a credit that look like one transfer, with Catat sebagai Transfer or Catat terpisah
@Composable
internal fun InboxPairCard(
    pair: InboxPairUi,
    actions: InboxItemActions,
    onPick: (InboxPicker) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fromName = pair.from.name.orEmpty()
    val toName = pair.target.name.orEmpty()
    ScreenCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(FintrackScreenSizes.rowPaddingHorizontal)) {
            InboxHeaderRow(
                source = fromName,
                title = stringResource(R.string.inbox_pair_title, fromName.ifEmpty { "?" }, toName.ifEmpty { "?" }),
                subtitle = "$fromName → $toName",
                amount = { InboxAmount(money = pair.amount, type = TransactionType.TRANSFER) },
            )
            PairActions(pair, actions, onPick)
        }
    }
}

// FR-091: the two wallet chips, then the split text and the confirm button of the pair
@Composable
private fun PairActions(
    pair: InboxPairUi,
    actions: InboxItemActions,
    onPick: (InboxPicker) -> Unit,
) {
    if (isLargeText()) {
        StackedPairActions(pair, actions, onPick)
        return
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PairPills(pair, onPick)
        Spacer(modifier = Modifier.weight(1f))
        PairButtons(pair, actions)
    }
}

// NFR-010: at large font sizes the chips wrap and the split and confirm buttons get a line of their own
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StackedPairActions(
    pair: InboxPairUi,
    actions: InboxItemActions,
    onPick: (InboxPicker) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium)) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap),
            verticalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap),
        ) { PairPills(pair, onPick) }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) { PairButtons(pair, actions) }
    }
}

// FR-091: the source and the target wallet chips
@Composable
private fun PairPills(
    pair: InboxPairUi,
    onPick: (InboxPicker) -> Unit,
) {
    val transfer = FintrackTheme.colors.semantics.transfer
    ScreenPill(
        label = pair.from.name ?: stringResource(R.string.inbox_pick_wallet),
        onClick = { onPick(InboxPicker(pair.fromItemId, PickKind.WALLET)) },
        look = PillLook(contentColor = transfer),
    )
    ScreenPill(
        label = pair.target.name ?: stringResource(R.string.inbox_pick_wallet),
        onClick = { onPick(InboxPicker(pair.toItemId, PickKind.TARGET_WALLET)) },
        look = PillLook(contentColor = transfer),
    )
}

@Composable
private fun PairButtons(
    pair: InboxPairUi,
    actions: InboxItemActions,
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
        icon =
            CircleIcon(
                iconRes = DesignR.drawable.ic_check,
                description = stringResource(R.string.inbox_pair_confirm),
                filled = true,
            ),
        onClick = { actions.onConfirmPair(pair.fromItemId) },
        enabled = pair.from.slotId != null && pair.target.slotId != null,
    )
}
