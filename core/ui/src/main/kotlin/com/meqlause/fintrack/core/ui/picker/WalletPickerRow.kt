package com.meqlause.fintrack.core.ui.picker

import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType

// S-18: one wallet line in the picker; balanceLabel is formatted by the caller, isBlocked shows "Sudah dipilih"
data class WalletPickerRow(
    val walletId: WalletId,
    val name: String,
    val type: WalletType,
    val balanceLabel: String,
    val isSelected: Boolean = false,
    val isBlocked: Boolean = false,
)
