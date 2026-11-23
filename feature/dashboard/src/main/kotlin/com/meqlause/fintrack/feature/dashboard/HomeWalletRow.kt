package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.WalletId

// FR-040: the wallet tiles under the hero, one per active wallet, scrolled sideways edge to edge
@Composable
internal fun HomeWalletRow(
    wallets: List<HomeWallet>,
    hidden: Boolean,
    onOpenWallet: (WalletId) -> Unit,
    onOpenCreditWallet: (WalletId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val positiveTotal = wallets.sumOf { it.balance.rupiah.coerceAtLeast(0L) }
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = FintrackTheme.spacing.gutter),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        items(items = wallets, key = { it.walletId.value }) { wallet ->
            val open = if (wallet.isCredit) onOpenCreditWallet else onOpenWallet
            HomeWalletChip(
                wallet = wallet,
                hidden = hidden,
                percent = sharePercent(wallet.balance.rupiah, positiveTotal),
                onClick = { open(wallet.walletId) },
            )
        }
    }
}

// FR-040: the wallet's share of the positive balances, rounded to a whole percent
private fun sharePercent(
    balance: Long,
    positiveTotal: Long,
): Int =
    if (positiveTotal <= 0L ||
        balance <= 0L
    ) {
        0
    } else {
        Math.round(balance * PERCENT.toDouble() / positiveTotal).toInt()
    }

private const val PERCENT = 100
