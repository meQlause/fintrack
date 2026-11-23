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
import com.meqlause.fintrack.core.ui.transaction.rowAppearanceOf
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
import java.util.Locale
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
            val related =
                rows
                    .filter { it.routing.walletId == walletId || it.routing.targetWalletId == walletId }
                    .sortedByDescending { it.timing.occurredAt }
                    .map { it.toRow(appearances) }
            return WalletDetailUiState.Success(WalletItem(wallet = wallet, balance = balance), related, open)
        }

        private fun Transaction.toRow(appearances: Map<CategoryId, Appearance>): WalletDetailRow =
            WalletDetailRow(
                transactionId = transactionId.value,
                title = memo.merchant.ifBlank { memo.note },
                subtitle = DATE_FORMAT.withZone(clock.zone).format(timing.occurredAt),
                amount = movement.amount,
                type = movement.type,
                appearance = rowAppearanceOf(movement.type, routing.categoryId?.let(appearances::get)),
            )

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
            val DATE_FORMAT: DateTimeFormatter =
                DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))
        }
    }
