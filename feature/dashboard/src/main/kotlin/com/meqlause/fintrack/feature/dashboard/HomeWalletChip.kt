package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// FR-040: one wallet tile with icon, share of the total, name and compact balance; a tap opens the wallet (S-19)
@Composable
internal fun HomeWalletChip(
    wallet: HomeWallet,
    hidden: Boolean,
    percent: Int,
    onClick: () -> Unit,
) {
    val shape = FintrackTheme.shapes.medium
    Column(
        modifier =
            Modifier
                .width(FintrackTheme.sizes.walletTileWidth)
                .clip(shape)
                .border(FintrackTheme.sizes.hairline, FintrackTheme.colors.surfaces.outline, shape)
                .background(FintrackTheme.colors.surfaces.surface)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(FintrackTheme.spacing.medium),
    ) {
        WalletChipHeader(wallet = wallet, percent = percent)
        Text(
            text = wallet.name,
            modifier = Modifier.padding(top = FintrackTheme.spacing.small + FintrackTheme.spacing.xxSmall),
            style = FintrackTheme.typography.caption,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        WalletChipBalance(wallet = wallet, hidden = hidden)
    }
}

// FR-040: the wallet icon on the left and its share of the total on the right
@Composable
private fun WalletChipHeader(
    wallet: HomeWallet,
    percent: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        IconCircle(
            iconKey = wallet.appearance.iconKey,
            colorKey = wallet.appearance.colorKey,
            size = FintrackTheme.sizes.walletChipIcon,
            glyph = FintrackTheme.sizes.walletChipGlyph,
            shape = FintrackTheme.shapes.walletIcon,
        )
        Text(
            text = stringResource(R.string.home_percent, percent),
            style = FintrackTheme.typography.micro,
            color = FintrackTheme.colors.texts.faint,
        )
    }
}

// FR-040: the compact balance, or the hidden mask when amounts are hidden
@Composable
private fun WalletChipBalance(
    wallet: HomeWallet,
    hidden: Boolean,
) {
    Text(
        text =
            if (hidden) {
                stringResource(R.string.home_amount_hidden)
            } else {
                MoneyFormatter.formatShort(wallet.balance)
            },
        style = FintrackTheme.typography.numberSmall,
        color = FintrackTheme.colors.texts.onSurface,
        maxLines = 1,
    )
}
