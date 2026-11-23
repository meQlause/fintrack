package com.meqlause.fintrack.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.bills.BillsCard
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.QuickEntryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.usecase.ObserveBillsCardUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveNetWorthUseCase
import com.meqlause.fintrack.core.model.QuickEntry
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.ui.shortcut.toTileParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import javax.inject.Inject

// FR-040..043, FR-039, S-03: drives Beranda; the repositories are the only source of balances and shortcuts
@HiltViewModel
internal class HomeViewModel
    @Inject
    constructor(
        wallets: WalletRepository,
        transactions: TransactionRepository,
        categories: CategoryRepository,
        settings: SettingsRepository,
        private val quickEntries: QuickEntryRepository,
        private val bills: ObserveBillsCardUseCase,
        private val netWorth: ObserveNetWorthUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val hideOverride = MutableStateFlow<Boolean?>(null)
        private val drawer = MutableStateFlow<HomeDrawer?>(null)
        private val removedShortcut = MutableStateFlow<QuickEntry?>(null)
        private var undoJob: Job? = null

        private val snapshot: Flow<HomeSnapshot> =
            combine(
                wallets.observeAll(),
                wallets.observeBalances(),
                transactions.observeAll(),
                categories.observeAll(),
                settings.observe(),
                ::HomeSnapshot,
            )

        // FR-137: the snapshot paired with the Tagihan card, so one emission carries every Beranda input
        private val snapshotWithBills: Flow<Triple<HomeSnapshot, BillsCard, NetWorth>> =
            combine(snapshot, bills(), netWorth()) { data, card, worth -> Triple(data, card, worth) }

        // S-03: Loading until the first snapshot, then Success; toggle, drawer and undo are screen-local state
        val state: StateFlow<HomeUiState> =
            combine(
                snapshotWithBills,
                hideOverride,
                drawer,
                quickEntries.observeAll(),
                removedShortcut,
            ) { (data, card, worth), hide, open, entries, gone ->
                HomeCalculator.success(data, card, worth, hide, open, clock).copy(
                    shortcuts = entries.toTileParams(data.wallets.associate { it.walletId to it.profile.name }),
                    removedShortcut = gone,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HomeUiState.Loading)

        // FR-040: flips the privacy toggle for this session; the stored hide-by-default setting is not changed
        fun toggleHidden() {
            val hidden = (state.value as? HomeUiState.Success)?.hidden ?: false
            hideOverride.value = !hidden
        }

        // FR-040: the summary pill opens the Ringkasan drawer
        fun openSummary() {
            drawer.value = HomeDrawer.SUMMARY
        }

        // FR-042: the category card opens the Kategori drawer
        fun openCategories() {
            drawer.value = HomeDrawer.CATEGORIES
        }

        // FR-039: the "Atur" link opens the shortcut manager drawer
        fun openShortcuts() {
            drawer.value = HomeDrawer.SHORTCUTS
        }

        fun closeDrawer() {
            drawer.value = null
        }

        // FR-039: deletes at once; the removed shortcut can be put back for five seconds
        fun deleteShortcut(quickEntryId: QuickEntryId) {
            viewModelScope.launch {
                val entry =
                    quickEntries.observeAll().first().firstOrNull { it.quickEntryId == quickEntryId }
                        ?: return@launch
                quickEntries.delete(quickEntryId)
                removedShortcut.value = entry
                undoJob?.cancel()
                undoJob =
                    viewModelScope.launch {
                        delay(UNDO_WINDOW_MS)
                        removedShortcut.value = null
                    }
            }
        }

        // FR-039: Urungkan puts the last deleted shortcut back
        fun restoreShortcut() {
            val entry = removedShortcut.value ?: return
            undoJob?.cancel()
            removedShortcut.value = null
            viewModelScope.launch { quickEntries.insert(entry) }
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
            const val UNDO_WINDOW_MS = 5_000L
        }
    }
