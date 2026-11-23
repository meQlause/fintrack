package com.meqlause.fintrack.feature.credit.instalment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.Wallet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

// FR-142, S-16: the instalment plans of every credit wallet, split into Aktif and Selesai; math stays in core:domain
@HiltViewModel
internal class InstalmentListViewModel
    @Inject
    constructor(
        plans: InstallmentPlanRepository,
        transactions: TransactionRepository,
        wallets: WalletRepository,
        private val clock: Clock,
    ) : ViewModel() {
        private val filter = MutableStateFlow(InstalmentFilter.ACTIVE)

        val state: StateFlow<InstalmentListUiState> =
            combine(plans.observeAll(), transactions.observeAll(), wallets.observeAll(), filter) {
                planRows,
                rows,
                all,
                selected,
                ->
                InstalmentListUiState.Success(rowsOf(planRows, rows, all, selected), selected)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), InstalmentListUiState.Loading)

        // S-16: the Aktif or Selesai segment; the list follows the choice at once
        fun pickFilter(selected: InstalmentFilter) {
            filter.value = selected
        }

        private fun rowsOf(
            planRows: List<InstallmentPlan>,
            rows: List<Transaction>,
            allWallets: List<Wallet>,
            selected: InstalmentFilter,
        ): List<InstalmentRow> {
            val today = LocalDate.now(clock)
            return planRows
                .mapNotNull { plan -> rowOf(plan, rows, allWallets, today) }
                .filter { it.isFinished == (selected == InstalmentFilter.FINISHED) }
        }

        private fun rowOf(
            plan: InstallmentPlan,
            rows: List<Transaction>,
            allWallets: List<Wallet>,
            today: LocalDate,
        ): InstalmentRow? {
            val wallet = allWallets.firstOrNull { it.walletId == plan.purchase.walletId } ?: return null
            val terms = wallet.terms.credit ?: return null
            return plan.toRow(wallet.profile.name, rows, terms.statementDay, today, clock.zone)
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
