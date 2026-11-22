package com.meqlause.fintrack.core.ui.picker

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// S-18: the wallet sections in drawer order, each with its icon header and the wallet types it holds
internal enum class WalletPickerGroup(
    @param:StringRes val titleRes: Int,
    @param:DrawableRes val iconRes: Int,
    val types: List<WalletType>,
) {
    CASH(R.string.picker_group_cash, DesignR.drawable.ic_wallet, listOf(WalletType.CASH)),
    DEBIT(R.string.picker_group_debit, DesignR.drawable.ic_wallet, listOf(WalletType.BANK, WalletType.EWALLET)),
    CREDIT_CARD(R.string.picker_group_credit, DesignR.drawable.ic_wallet, listOf(WalletType.CREDIT_CARD)),
    PAYLATER(R.string.picker_group_paylater, DesignR.drawable.ic_wallet, listOf(WalletType.PAYLATER)),
    INVESTMENT(R.string.picker_group_invest, DesignR.drawable.ic_trend, listOf(WalletType.INVESTMENT)),
    ;

    // S-18: credit sections are hidden where credit is not allowed; investments only in transfers
    fun isAllowed(
        allowCredit: Boolean,
        allowInvestment: Boolean,
    ): Boolean =
        when (this) {
            CASH, DEBIT -> true
            CREDIT_CARD, PAYLATER -> allowCredit
            INVESTMENT -> allowInvestment
        }
}
