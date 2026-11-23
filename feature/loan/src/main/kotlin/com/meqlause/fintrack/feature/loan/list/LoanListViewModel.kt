package com.meqlause.fintrack.feature.loan.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.domain.usecase.ObserveLoansUseCase
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

// FR-153, S-17: the loans of the chosen direction; the balances come from core:domain
@HiltViewModel
internal class LoanListViewModel
    @Inject
    constructor(
        observeLoans: ObserveLoansUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val direction = MutableStateFlow(LoanDirection.LENT)

        val state: StateFlow<LoanListUiState> =
            combine(observeLoans(), direction) { entries, selected ->
                buildState(entries, selected)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), LoanListUiState())

        // S-17: the Piutang or Hutang segment; the list follows the choice at once
        fun pickDirection(selected: LoanDirection) {
            direction.value = selected
        }

        private fun buildState(
            entries: List<LoanEntry>,
            selected: LoanDirection,
        ): LoanListUiState {
            val today = LocalDate.now(clock)
            val rows =
                entries
                    .filter { it.loan.agreement.direction == selected }
                    .map { it.toListRow(today, clock.zone) }
            val (open, finished) = rows.partition { it.status == LoanStatus.OPEN }
            return LoanListUiState(direction = selected, groups = open.groupedByPerson(), settled = finished)
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
