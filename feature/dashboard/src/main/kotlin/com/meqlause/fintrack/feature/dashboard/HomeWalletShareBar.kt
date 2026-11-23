package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-040: each wallet's share of the positive balances as slim rounded segments in the wallet colours
@Composable
internal fun HomeWalletShareBar(
    wallets: List<HomeWallet>,
    modifier: Modifier = Modifier,
) {
    val positive = wallets.filter { it.balance.rupiah > 0L }
    if (positive.isEmpty()) return
    Row(
        modifier = modifier.fillMaxWidth().height(FintrackTheme.sizes.heroBarHeight),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xxSmall),
    ) {
        positive.forEach { wallet ->
            Box(
                modifier =
                    Modifier
                        .weight(wallet.balance.rupiah.toFloat())
                        .fillMaxHeight()
                        .clip(FintrackTheme.shapes.full)
                        .background(FintrackTheme.colors.categoryColor(wallet.appearance.colorKey)),
            )
        }
    }
}
