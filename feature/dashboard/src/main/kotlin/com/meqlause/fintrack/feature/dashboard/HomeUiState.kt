package com.meqlause.fintrack.feature.dashboard

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.ui.shortcut.ShortcutTileParams

// FR-040..043, S-03: Loading until the first snapshot arrives, then Success with every Beranda block
internal sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val hidden: Boolean,
        val total: Money,
        val wallets: List<HomeWallet>,
        val period: HomePeriod,
        val categories: List<HomeCategoryShare>,
        val recent: List<HomeRecent>,
        val drawer: HomeDrawer?,
        val shortcuts: List<ShortcutTileParams> = emptyList(),
        val removedShortcut: QuickEntry? = null,
    ) : HomeUiState
}
