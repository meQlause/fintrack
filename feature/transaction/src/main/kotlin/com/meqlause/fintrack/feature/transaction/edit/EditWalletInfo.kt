package com.meqlause.fintrack.feature.transaction.edit

import androidx.annotation.StringRes
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

private val FALLBACK_APPEARANCE = Appearance(iconKey = "wallet", colorKey = "gray")

// S-04, DOC-08: what a wallet row of the sheet shows, the name, the compact balance and the icon of the wallet
internal data class EditWalletInfo(
    val name: String,
    val balance: String,
    val appearance: Appearance,
    val groupRes: Int?,
) {
    companion object {
        fun of(
            state: EditTransactionUiState,
            walletId: WalletId?,
        ): EditWalletInfo {
            val wallet = state.catalog.wallets.firstOrNull { it.walletId == walletId }
            val money = state.catalog.moneyByWallet[walletId] ?: Money(0L)
            return EditWalletInfo(
                name = wallet?.profile?.name.orEmpty(),
                balance = if (wallet == null) "" else MoneyFormatter.formatShort(money),
                appearance = wallet?.profile?.appearance ?: FALLBACK_APPEARANCE,
                groupRes = wallet?.let(::groupOf),
            )
        }

        // the prototype groups wallets as Tunai, Debit, Kartu kredit and Paylater
        @StringRes
        private fun groupOf(wallet: Wallet): Int =
            when (wallet.kind.type) {
                WalletType.CASH -> R.string.transaction_group_cash
                WalletType.BANK, WalletType.EWALLET -> R.string.transaction_group_debit
                WalletType.CREDIT_CARD -> R.string.transaction_group_credit
                WalletType.PAYLATER -> R.string.transaction_group_paylater
                WalletType.INVESTMENT, WalletType.OTHER -> R.string.transaction_group_other
            }
    }
}
