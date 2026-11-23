package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId

// DOC-09, S-03: every action of Beranda; the defaults keep previews and tests free of wiring
internal data class HomeCallbacks(
    val onOpenSettings: () -> Unit = {},
    val onOpenInbox: () -> Unit = {},
    val onToggleTheme: (Boolean) -> Unit = {},
    val onToggleHidden: () -> Unit = {},
    val onOpenSummary: () -> Unit = {},
    val onOpenCategories: () -> Unit = {},
    val onCloseDrawer: () -> Unit = {},
    val onOpenWallet: (WalletId) -> Unit = {},
    val onOpenWallets: () -> Unit = {},
    val onOpenCreditWallet: (WalletId) -> Unit = {},
    val onOpenInstalments: () -> Unit = {},
    val onOpenLoans: () -> Unit = {},
    val onQuickEntry: () -> Unit = {},
    val onOpenTransaction: (TransactionId) -> Unit = {},
    val onOpenHistory: () -> Unit = {},
    val onApplyShortcut: (QuickEntryId) -> Unit = {},
    val onOpenShortcuts: () -> Unit = {},
    val onDeleteShortcut: (QuickEntryId) -> Unit = {},
    val onRestoreShortcut: () -> Unit = {},
)
