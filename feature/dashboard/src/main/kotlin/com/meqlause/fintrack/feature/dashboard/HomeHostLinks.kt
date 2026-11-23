package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId

/** DOC-09, S-03: leaving Beranda for a screen the host app owns. */
public data class HomeHostLinks(
    val onOpenSettings: () -> Unit = {},
    val onOpenInbox: () -> Unit = {},
    val onOpenWallet: (WalletId) -> Unit = {},
    val onOpenCreditWallet: (WalletId) -> Unit = {},
    val onOpenWallets: () -> Unit = {},
    val onOpenTransaction: (TransactionId) -> Unit = {},
)
