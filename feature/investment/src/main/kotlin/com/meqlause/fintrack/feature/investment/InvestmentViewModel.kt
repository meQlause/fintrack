package com.meqlause.fintrack.feature.investment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.usecase.CreateInvestmentUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveInvestmentPortfolioUseCase
import com.meqlause.fintrack.core.model.InvestKind
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import javax.inject.Inject

// FR-170, FR-173, S-08: drives the Investasi tab; the use cases are the only source of accounts and totals
@HiltViewModel
internal class InvestmentViewModel
    @Inject
    constructor(
        observePortfolio: ObserveInvestmentPortfolioUseCase,
        private val createInvestment: CreateInvestmentUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val draft = MutableStateFlow<InvestmentDraft?>(null)

        // S-08: Loading until the first portfolio arrives, then the accounts, the totals and the activity
        val state: StateFlow<InvestmentUiState> =
            combine(observePortfolio(), draft) { portfolio, open ->
                InvestmentUiState(portfolio = portfolio, draft = open)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), InvestmentUiState())

        // FR-170: "Tambah investasi" opens an empty creation sheet
        fun startCreate() {
            draft.value = InvestmentDraft()
        }

        fun dismissDraft() {
            draft.value = null
        }

        fun changeName(name: String) {
            draft.update { it?.copy(name = name.take(MAX_NAME_LENGTH), nameRejected = false) }
        }

        fun openKindPicker(open: Boolean) {
            draft.update { it?.copy(kindPickerOpen = open) }
        }

        fun pickKind(kind: InvestKind) {
            draft.update { it?.copy(kind = kind, kindPickerOpen = false) }
        }

        fun typeDigit(digit: String) {
            draft.update { open -> open?.let { it.copy(invested = KeypadDigits.append(it.invested, digit)) } }
        }

        fun backspace() {
            draft.update { open -> open?.let { it.copy(invested = KeypadDigits.backspace(it.invested)) } }
        }

        // FR-170: the sheet closes when the account is stored; a rejected name keeps it open with a message
        fun save() {
            val open = draft.value ?: return
            val kind = open.kind ?: return
            viewModelScope.launch {
                val money = KeypadDigits.toMoney(open.invested)
                val createdId = createInvestment(open.name, kind, money, clock.instant())
                if (createdId == null) {
                    draft.update { it?.copy(nameRejected = true) }
                } else {
                    draft.value = null
                }
            }
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
            const val MAX_NAME_LENGTH = 30
        }
    }
