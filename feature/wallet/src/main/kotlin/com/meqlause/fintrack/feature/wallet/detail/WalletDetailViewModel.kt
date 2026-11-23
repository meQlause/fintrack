package com.meqlause.fintrack.feature.wallet.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.transaction.RowAppearance
import com.meqlause.fintrack.feature.wallet.WalletItem
import com.meqlause.fintrack.feature.wallet.navigation.WalletDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// FR-017, S-19: drives the wallet detail; the route gives the wallet id, the repositories give everything else
@HiltViewModel
internal class WalletDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val wallets: WalletRepository,
        private val transactions: TransactionRepository,
        private val categories: CategoryRepository,
        private val clock: Clock,
    ) : ViewModel() {
        val walletId: WalletId = WalletId(savedStateHandle.toRoute<WalletDetailDestination>().walletId)
        private val expanded = MutableStateFlow(false)

        // S-19: Loading until the first lists arrive, then Missing or Success
        val state: StateFlow<WalletDetailUiState> =
            combine(
                wallets.observeAll(),
                wallets.observeBalances(),
                transactions.observeAll(),
                categories.observeAll(),
                expanded,
            ) { all, balances, rows, categoryList, open ->
                buildState(all, balances, rows, categoryList, open)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), WalletDetailUiState.Loading)

        // FR-017: "Lihat semua (N)" expands the list in place; "Ringkas" collapses it again
        fun toggleExpanded() {
            expanded.update { !it }
        }

        private fun buildState(
            allWallets: List<Wallet>,
            balances: Map<WalletId, Money>,
            rows: List<Transaction>,
            categoryList: List<Category>,
            open: Boolean,
        ): WalletDetailUiState {
            val appearances = categoryList.associate { it.categoryId to it.profile.appearance }
            val wallet = allWallets.firstOrNull { it.walletId == walletId } ?: return WalletDetailUiState.Missing
            val balance = balances[walletId] ?: wallet.terms.initialBalance
            val names = allWallets.associate { it.walletId to it.profile.name }
            val mine = rows.filter { it.routing.walletId == walletId || it.routing.targetWalletId == walletId }
            val related = mine.sortedByDescending { it.timing.occurredAt }.map { it.toRow(appearances, names) }
            return WalletDetailUiState.Success(
                item = WalletItem(wallet = wallet, balance = balance),
                rows = related,
                expanded = open,
                stats = WalletDetailStats.of(walletId, mine, clock),
            )
        }

        // S-19: "BCA · 12:30" or, for a transfer, "BCA → BCA Card · 10:00"
        private fun Transaction.toRow(
            appearances: Map<CategoryId, Appearance>,
            names: Map<WalletId, String>,
        ): WalletDetailRow =
            WalletDetailRow(
                transactionId = transactionId.value,
                title = memo.merchant.ifBlank { memo.note },
                subtitle = subtitleOf(names),
                amount = movement.amount,
                type = movement.type,
                appearance = RowAppearance.forType(movement.type, routing.categoryId?.let(appearances::get)),
            )

        private fun Transaction.subtitleOf(names: Map<WalletId, String>): String {
            val from = names[routing.walletId].orEmpty()
            val target = routing.targetWalletId?.let { names[it] }
            val route = if (target == null) from else "$from → $target"
            return route + " · " + TIME_FORMAT.withZone(clock.zone).format(timing.occurredAt)
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
            val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        }
    }
