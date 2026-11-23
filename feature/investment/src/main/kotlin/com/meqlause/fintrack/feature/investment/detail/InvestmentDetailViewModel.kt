package com.meqlause.fintrack.feature.investment.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.meqlause.fintrack.core.domain.usecase.ObserveInvestmentDetailUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveValuationUseCase
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.feature.investment.KeypadDigits
import com.meqlause.fintrack.feature.investment.navigation.InvestmentDetailDestination
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

// FR-172, FR-173, S-20: drives the investment detail; the route gives the wallet id, the use cases give everything else
@HiltViewModel
internal class InvestmentDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        observeDetail: ObserveInvestmentDetailUseCase,
        private val saveValuation: SaveValuationUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val walletId = WalletId(savedStateHandle.toRoute<InvestmentDetailDestination>().walletId)
        private val valuation = MutableStateFlow<String?>(null)

        // S-20: Loading until the first emission, then Missing (detail is null) or Success
        val state: StateFlow<InvestmentDetailUiState> =
            combine(observeDetail(walletId), valuation) { detail, digits ->
                InvestmentDetailUiState(loaded = true, detail = detail, valuation = digits)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), InvestmentDetailUiState())

        // FR-172: "Update nilai" opens the keypad prefilled with the current value
        fun openValuation() {
            val current =
                state.value.detail
                    ?.account
                    ?.valuation
                    ?.currentValue
                    ?.rupiah ?: 0L
            valuation.value = current.toString()
        }

        fun dismissValuation() {
            valuation.value = null
        }

        fun typeDigit(digit: String) {
            valuation.update { digits -> digits?.let { KeypadDigits.append(it, digit) } }
        }

        fun backspace() {
            valuation.update { digits -> digits?.let { KeypadDigits.backspace(it) } }
        }

        // FR-172, BR-054: the sheet closes once the value is stored
        fun save() {
            val digits = valuation.value ?: return
            viewModelScope.launch {
                val money = KeypadDigits.toMoney(digits)
                if (saveValuation(walletId, money, clock.instant(), clock.zone)) valuation.value = null
            }
        }

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
