package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId

// DOC-09, S-03: every action of Beranda, grouped by where it leads; the defaults keep previews and tests free of wiring
internal data class HomeCallbacks(
    val host: HomeHostLinks = HomeHostLinks(),
    val entry: HomeEntryLinks = HomeEntryLinks(),
    val screen: Screen = Screen(),
    val shortcuts: Shortcuts = Shortcuts(),
) {
    val onOpenSettings: () -> Unit get() = host.onOpenSettings
    val onOpenInbox: () -> Unit get() = host.onOpenInbox
    val onOpenWallet: (WalletId) -> Unit get() = host.onOpenWallet
    val onOpenCreditWallet: (WalletId) -> Unit get() = host.onOpenCreditWallet
    val onOpenWallets: () -> Unit get() = host.onOpenWallets
    val onOpenTransaction: (TransactionId) -> Unit get() = host.onOpenTransaction
    val onOpenHistory: () -> Unit get() = entry.onOpenHistory
    val onOpenInstalments: () -> Unit get() = entry.onOpenInstalments
    val onOpenLoans: () -> Unit get() = entry.onOpenLoans
    val onQuickEntry: () -> Unit get() = entry.onQuickEntry
    val onApplyShortcut: (QuickEntryId) -> Unit get() = entry.onApplyShortcut
    val onToggleTheme: (Boolean) -> Unit get() = screen.onToggleTheme
    val onToggleHidden: () -> Unit get() = screen.onToggleHidden
    val onOpenSummary: () -> Unit get() = screen.onOpenSummary
    val onOpenCategories: () -> Unit get() = screen.onOpenCategories
    val onCloseDrawer: () -> Unit get() = screen.onCloseDrawer
    val onOpenShortcuts: () -> Unit get() = screen.onOpenShortcuts
    val onDeleteShortcut: (QuickEntryId) -> Unit get() = shortcuts.onDeleteShortcut
    val onRestoreShortcut: () -> Unit get() = shortcuts.onRestoreShortcut

    // actions that stay on Beranda: theme, privacy toggle and the drawers
    internal data class Screen(
        val onToggleTheme: (Boolean) -> Unit = {},
        val onToggleHidden: () -> Unit = {},
        val onOpenSummary: () -> Unit = {},
        val onOpenCategories: () -> Unit = {},
        val onCloseDrawer: () -> Unit = {},
        val onOpenShortcuts: () -> Unit = {},
    )

    // the shortcut manager: delete now, and the undo that puts the last one back
    internal data class Shortcuts(
        val onDeleteShortcut: (QuickEntryId) -> Unit = {},
        val onRestoreShortcut: () -> Unit = {},
    )
}
