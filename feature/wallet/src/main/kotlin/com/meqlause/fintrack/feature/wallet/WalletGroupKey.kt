package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.model.WalletType

// S-09: the groups of the Dompet list, in display order; credit cards and paylater share Kredit
internal enum class WalletGroupKey {
    CASH,
    BANK,
    EWALLET,
    CREDIT,
    INVESTMENT,
    OTHER,
    ;

    companion object {
        // S-09: the group a wallet type is listed under
        fun of(type: WalletType): WalletGroupKey =
            when (type) {
                WalletType.CASH -> CASH
                WalletType.BANK -> BANK
                WalletType.EWALLET -> EWALLET
                WalletType.CREDIT_CARD, WalletType.PAYLATER -> CREDIT
                WalletType.INVESTMENT -> INVESTMENT
                WalletType.OTHER -> OTHER
            }
    }
}
