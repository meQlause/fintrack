package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.WalletType

// S-09, S-18: the Indonesian label resource of each wallet type, group and investment kind
internal object WalletLabels {
    // FR-010: the label of a wallet type in the list and in the type drawer
    fun type(type: WalletType): Int =
        when (type) {
            WalletType.CASH -> R.string.wallet_type_cash
            WalletType.BANK -> R.string.wallet_type_bank
            WalletType.EWALLET -> R.string.wallet_type_ewallet
            WalletType.CREDIT_CARD -> R.string.wallet_type_credit_card
            WalletType.PAYLATER -> R.string.wallet_type_paylater
            WalletType.INVESTMENT -> R.string.wallet_type_investment
            WalletType.OTHER -> R.string.wallet_type_other
        }

    // S-09: the heading of a group in the Dompet list
    fun group(group: WalletGroupKey): Int =
        when (group) {
            WalletGroupKey.CASH -> R.string.wallet_group_cash
            WalletGroupKey.BANK -> R.string.wallet_group_bank
            WalletGroupKey.EWALLET -> R.string.wallet_group_ewallet
            WalletGroupKey.CREDIT -> R.string.wallet_group_credit
            WalletGroupKey.INVESTMENT -> R.string.wallet_group_investment
            WalletGroupKey.OTHER -> R.string.wallet_group_other
        }

    // FR-170: the label of an investment kind in the kind drawer
    fun investKind(kind: InvestKind): Int =
        when (kind) {
            InvestKind.MUTUAL_FUND -> R.string.wallet_kind_mutual_fund
            InvestKind.STOCK -> R.string.wallet_kind_stock
            InvestKind.GOLD -> R.string.wallet_kind_gold
            InvestKind.DEPOSIT -> R.string.wallet_kind_deposit
            InvestKind.CRYPTO -> R.string.wallet_kind_crypto
            InvestKind.OTHER -> R.string.wallet_kind_other
        }
}
