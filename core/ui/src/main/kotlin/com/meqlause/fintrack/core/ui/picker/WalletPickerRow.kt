package com.meqlause.fintrack.core.ui.picker

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType

// S-18: one wallet line in the picker; shownBalanceText is formatted by the caller, isBlocked shows "Sudah dipilih"
data class WalletPickerRow(
    val walletId: WalletId,
    val name: String,
    val type: WalletType,
    val shownBalanceText: String,
    val mark: PickerMark = PickerMark(),
    val appearance: Appearance = Appearance("wallet", "gray"),
)
