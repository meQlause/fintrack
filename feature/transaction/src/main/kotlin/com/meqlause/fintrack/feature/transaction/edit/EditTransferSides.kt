package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.zIndex
import com.meqlause.fintrack.core.designsystem.icon.IconBox
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.icon.TintedIconBox
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// S-04: the caption of a transfer side, the wallet it shows and whether it aligns to the end
private data class SideInfo(
    val label: String,
    val info: EditWalletInfo,
    val alignEnd: Boolean,
)

// S-04: Dari and Ke as two cards with a dark swap button between them; tapping a card opens its wallet drawer
@Composable
internal fun EditTransferSides(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TransferSide(
            side =
                SideInfo(
                    label = stringResource(R.string.transaction_transfer_from),
                    info = EditWalletInfo.forWallet(state, state.form.walletId),
                    alignEnd = false,
                ),
            onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.WALLET)) },
            modifier = Modifier.weight(1f),
        )
        SwapButton(onClick = { onEvent(EditTransactionEvent.WalletsSwapped) })
        TransferSide(
            side =
                SideInfo(
                    label = stringResource(R.string.transaction_transfer_to),
                    info = EditWalletInfo.forWallet(state, state.form.targetWalletId),
                    alignEnd = true,
                ),
            onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.TARGET_WALLET)) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SwapButton(onClick: () -> Unit) {
    val colors = FintrackTheme.colors
    val label = stringResource(R.string.transaction_swap_description)
    Box(
        modifier = Modifier.width(FintrackScreenSizes.swapSlot).zIndex(1f),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .requiredSize(FintrackScreenSizes.circleButton + FintrackScreenSizes.swapRing * 2)
                    .background(colors.surfaces.background, CircleShape)
                    .padding(FintrackScreenSizes.swapRing)
                    .clip(CircleShape)
                    .background(colors.texts.onSurface)
                    .clickable(onClick = onClick)
                    .semantics { contentDescription = label },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_arrow_r),
                contentDescription = null,
                tint = colors.surfaces.background,
                modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
            )
        }
    }
}

@Composable
private fun TransferSide(
    side: SideInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val (label, info, alignEnd) = side
    val shape = FintrackTheme.shapes.medium
    Column(
        modifier =
            modifier
                .clip(shape)
                .background(colors.surfaces.surface)
                .border(FintrackScreenSizes.sideBorder, colors.surfaces.outline, shape)
                .clickable(onClick = onClick)
                .padding(FintrackTheme.spacing.medium),
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
    ) {
        Text(text = label, style = FintrackScreenText.label(), color = colors.texts.onSurfaceVariant)
        SideName(info = info, alignEnd = alignEnd)
        Text(
            text = info.fundsText,
            style = FintrackScreenText.number(size = 11.5f, weight = 400),
            color = colors.texts.onSurfaceVariant,
            modifier = Modifier.padding(top = FintrackScreenSizes.pillGap),
        )
    }
}

@Composable
private fun SideName(
    info: EditWalletInfo,
    alignEnd: Boolean,
) {
    Row(
        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        if (!alignEnd) SideIcon(info)
        Text(
            text = info.name,
            style = FintrackScreenText.style(size = 14.5f, weight = 700),
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (alignEnd) SideIcon(info)
    }
}

@Composable
private fun SideIcon(info: EditWalletInfo) {
    TintedIconBox(
        look = IconLook(iconKey = info.appearance.iconKey, colorKey = info.appearance.colorKey),
        tile =
            IconBox(
                size = FintrackScreenSizes.smallIconBox,
                glyph = FintrackScreenSizes.circleGlyph,
                shape = RoundedCornerShape(FintrackScreenSizes.smallIconCorner),
            ),
    )
}
