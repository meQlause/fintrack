package com.meqlause.fintrack.feature.credit.instalment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.feature.credit.navigation.InstalmentDetailDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

// FR-144, FR-145, S-16: the schedule of one plan and its early payoff and delete actions
@HiltViewModel
internal class InstalmentDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val sources: InstalmentSources,
        private val actions: InstalmentActions,
        private val clock: Clock,
    ) : ViewModel() {
        private val planId = InstallmentPlanId(savedStateHandle.toRoute<InstalmentDetailDestination>().planId)
        private val session = MutableStateFlow(Session())

        val state: StateFlow<InstalmentDetailUiState> =
            combine(
                sources.plans.observeAll(),
                sources.transactions.observeAll(),
                sources.wallets.observeAll(),
                session,
            ) {
                planRows,
                rows,
                all,
                current,
                ->
                buildState(planRows, rows, all, current)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), InstalmentDetailUiState.Loading)

        // FR-144: all remaining portions are billed on the next statement (BR-037)
        fun payOff() {
            viewModelScope.launch { actions.payOffInstalment(planId) }
        }

        fun requestDelete() {
            session.update { it.copy(confirmDelete = true) }
        }

        fun dismissDelete() {
            session.update { it.copy(confirmDelete = false) }
        }

        // FR-145: the screen closes first so the removed plan is not shown as missing while it goes
        fun confirmDelete() {
            session.update { it.copy(closed = true) }
            viewModelScope.launch { actions.deleteInstalment(planId) }
        }

        private fun buildState(
            planRows: List<InstallmentPlan>,
            rows: List<Transaction>,
            allWallets: List<Wallet>,
            current: Session,
        ): InstalmentDetailUiState {
            if (current.closed) return InstalmentDetailUiState.Closed
            val planRow = rowOf(planRows, rows, allWallets) ?: return InstalmentDetailUiState.Missing
            return InstalmentDetailUiState.Success(planRow, current.confirmDelete)
        }

        // BR-035: the plan of this screen as a row, or null when the plan, its card or its terms are gone
        private fun rowOf(
            planRows: List<InstallmentPlan>,
            rows: List<Transaction>,
            allWallets: List<Wallet>,
        ): InstalmentRow? {
            val plan = planRows.firstOrNull { it.installmentPlanId == planId } ?: return null
            val wallet = allWallets.firstOrNull { it.walletId == plan.purchase.walletId } ?: return null
            val terms = wallet.terms.credit ?: return null
            val cutOff = StatementCutOff(statementDay = terms.statementDay, today = LocalDate.now(clock))
            return plan.toRow(wallet.profile.name, rows, cutOff, clock.zone)
        }

        private data class Session(
            val confirmDelete: Boolean = false,
            val closed: Boolean = false,
        )

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
