package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-040: one wallet as name and balance; tapping it opens the wallet detail (S-19)
@Composable
internal fun HomeWalletChip(
    wallet: HomeWallet,
    hidden: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = FintrackTheme.shapes.medium
    Column(
        modifier =
            modifier
                .clip(shape)
                .border(FintrackTheme.sizes.hairline, FintrackTheme.colors.surfaces.outline, shape)
                .background(FintrackTheme.colors.surfaces.surface)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = FintrackTheme.spacing.medium, vertical = FintrackTheme.spacing.small),
    ) {
        Text(
            text = wallet.name,
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        HomeAmountText(money = wallet.balance, hidden = hidden, style = FintrackTheme.typography.bodyLarge)
    }
}
