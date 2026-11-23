package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.WalletId

// FR-040: the wallet chips under the hero, one per active wallet, scrolled sideways
@Composable
internal fun HomeWalletRow(
    wallets: List<HomeWallet>,
    hidden: Boolean,
    onOpenWallet: (WalletId) -> Unit,
    onOpenCreditWallet: (WalletId) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        items(items = wallets, key = { it.walletId.value }) { wallet ->
            val open = if (wallet.isCredit) onOpenCreditWallet else onOpenWallet
            HomeWalletChip(wallet = wallet, hidden = hidden, onClick = { open(wallet.walletId) })
        }
    }
}
