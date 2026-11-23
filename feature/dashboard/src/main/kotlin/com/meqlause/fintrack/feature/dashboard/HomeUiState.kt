package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.ui.shortcut.ShortcutTileParams

// FR-040..043, S-03: Loading until the first snapshot arrives, then Success with every Beranda block
internal sealed interface HomeUiState {
    data object Loading : HomeUiState

    // the blocks are grouped into ledger and extras so the constructor stays within the parameter limit
    data class Success(
        val hidden: Boolean,
        val netWorth: NetWorth,
        val drawer: HomeDrawer?,
        val ledger: Ledger,
        val extras: Extras,
    ) : HomeUiState {
        val total: Money get() = ledger.total
        val wallets: List<HomeWallet> get() = ledger.wallets
        val period: HomePeriod get() = ledger.period
        val categories: List<HomeCategoryShare> get() = ledger.categories
        val recent: List<HomeRecent> get() = ledger.recent
        val bills: HomeBills get() = extras.bills
        val inbox: HomeInbox get() = extras.inbox
        val shortcuts: List<ShortcutTileParams> get() = extras.shortcuts
        val removedShortcut: QuickEntry? get() = extras.removedShortcut

        // FR-040..043: the balances, the period totals, the category shares and the newest transactions
        data class Ledger(
            val total: Money,
            val wallets: List<HomeWallet>,
            val period: HomePeriod,
            val categories: List<HomeCategoryShare>,
            val recent: List<HomeRecent>,
        )

        // FR-137, FR-084, FR-039: the Tagihan card, the inbox badge and the catat cepat tiles with their undo slot
        data class Extras(
            val bills: HomeBills = HomeBills(emptyList(), Money(0L)),
            val inbox: HomeInbox = HomeInbox(),
            val shortcuts: List<ShortcutTileParams> = emptyList(),
            val removedShortcut: QuickEntry? = null,
        )
    }
}
