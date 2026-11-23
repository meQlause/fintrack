package com.meqlause.fintrack.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import javax.inject.Inject

// FR-040..043, S-03: drives Beranda; the repositories are the only source of balances, transactions and categories
@HiltViewModel
internal class HomeViewModel
    @Inject
    constructor(
        wallets: WalletRepository,
        transactions: TransactionRepository,
        categories: CategoryRepository,
        settings: SettingsRepository,
        private val clock: Clock,
    ) : ViewModel() {
        private val hideOverride = MutableStateFlow<Boolean?>(null)
        private val drawer = MutableStateFlow<HomeDrawer?>(null)

        private val snapshot: Flow<HomeSnapshot> =
            combine(
                wallets.observeAll(),
                wallets.observeBalances(),
                transactions.observeAll(),
                categories.observeAll(),
                settings.observe(),
                ::HomeSnapshot,
            )

        // S-03: Loading until the first snapshot, then Success; the toggle and the drawer are screen-local state
        val state: StateFlow<HomeUiState> =
            combine(snapshot, hideOverride, drawer) { data, hide, open ->
                HomeCalculator.success(data, hide, open, clock)
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

        fun closeDrawer() {
            drawer.value = null
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
