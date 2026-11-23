package com.meqlause.fintrack.feature.loan.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.meqlause.fintrack.core.domain.loan.LoanDueOption
import com.meqlause.fintrack.core.domain.loan.RepaymentResult
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.usecase.ChangeLoanDueDateUseCase
import com.meqlause.fintrack.core.domain.usecase.DeleteLoanUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveLoansUseCase
import com.meqlause.fintrack.core.domain.usecase.RecordRepaymentUseCase
import com.meqlause.fintrack.core.domain.usecase.WriteOffLoanUseCase
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.feature.loan.navigation.LoanDetailDestination
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

private const val MAX_DIGITS = 15

// FR-152, FR-155, FR-158, S-17: one loan with its repayments, write-off and due date; the rules live in core:domain
@HiltViewModel
internal class LoanDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        observeLoans: ObserveLoansUseCase,
        wallets: WalletRepository,
        private val recordRepayment: RecordRepaymentUseCase,
        private val writeOff: WriteOffLoanUseCase,
        private val changeDueDate: ChangeLoanDueDateUseCase,
        private val deleteLoan: DeleteLoanUseCase,
        private val clock: Clock,
    ) : ViewModel() {
        private val loanId = LoanId(savedStateHandle.toRoute<LoanDetailDestination>().loanId)
        private val session = MutableStateFlow(LoanDetailUiState())

        val state: StateFlow<LoanDetailUiState> =
            combine(observeLoans(), wallets.observeAll(), session) { entries, all, current ->
                current.copy(
                    entry = entries.firstOrNull { it.loan.loanId == loanId },
                    isLoaded = true,
                    wallets = all.filterNot { it.status.isArchived },
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), LoanDetailUiState())

        fun onEvent(event: LoanDetailEvent) {
            when (event) {
                LoanDetailEvent.OpenRepayment -> {
                    openRepayment()
                }

                LoanDetailEvent.OpenDueDate -> {
                    session.update { it.copy(sheet = LoanDetailSheet.DUE_DATE) }
                }

                LoanDetailEvent.DismissSheet -> {
                    session.update { it.copy(sheet = null) }
                }

                is LoanDetailEvent.DigitTyped -> {
                    session.update { it.withAmount { digits -> digits + event.digit } }
                }

                LoanDetailEvent.BackspacePressed -> {
                    session.update { it.withAmount { digits -> digits.dropLast(1) } }
                }

                is LoanDetailEvent.WalletPicked -> {
                    session.update {
                        it.copy(
                            repayment = it.repayment.copy(walletId = event.walletId),
                        )
                    }
                }

                LoanDetailEvent.SaveRepayment -> {
                    saveRepayment()
                }

                is LoanDetailEvent.DuePicked -> {
                    pickDue(event.option)
                }

                is LoanDetailEvent.DialogOpened -> {
                    session.update { it.copy(dialog = event.dialog) }
                }

                LoanDetailEvent.DialogDismissed -> {
                    session.update { it.copy(dialog = null) }
                }

                LoanDetailEvent.DialogConfirmed -> {
                    confirmDialog()
                }
            }
        }

        // FR-152: the wallet the loan was opened from is preselected; the keypad starts empty
        private fun openRepayment() {
            val opening =
                state.value.entry
                    ?.movements
                    ?.firstOrNull()
                    ?.routing
                    ?.walletId
            session.update {
                it.copy(
                    sheet = LoanDetailSheet.REPAYMENT,
                    repayment = RepaymentForm(walletId = opening),
                    errors = emptySet(),
                )
            }
        }

        // FR-152: a recorded repayment closes the sheet; a rejected one keeps it open with the reasons
        private fun saveRepayment() {
            viewModelScope.launch {
                val form = session.value.repayment
                when (val result = recordRepayment(loanId, form.amount, form.walletId)) {
                    is RepaymentResult.Saved -> {
                        session.update {
                            it.copy(
                                sheet = null,
                                repayment = RepaymentForm(),
                                errors = emptySet(),
                            )
                        }
                    }

                    is RepaymentResult.Rejected -> {
                        session.update { it.copy(errors = result.errors) }
                    }
                }
            }
        }

        // FR-158: the chosen option counts from today; "Tidak ada" clears the due date
        private fun pickDue(option: LoanDueOption) {
            viewModelScope.launch {
                changeDueDate(loanId, option.dueFrom(LocalDate.now(clock)))
                session.update { it.copy(sheet = null) }
            }
        }

        // FR-155, BR-046: the confirmed write-off or delete; deleting leaves the screen
        private fun confirmDialog() {
            val dialog = session.value.dialog ?: return
            viewModelScope.launch {
                if (dialog == LoanDialog.WRITE_OFF) writeOff(loanId) else deleteLoan(loanId)
                session.update { it.copy(dialog = null, finished = dialog == LoanDialog.DELETE) }
            }
        }

        private fun LoanDetailUiState.withAmount(transform: (String) -> String): LoanDetailUiState =
            copy(
                repayment =
                    repayment.copy(
                        amountDigits = transform(repayment.amountDigits).trimStart('0').take(MAX_DIGITS),
                    ),
                errors = emptySet(),
            )

        private companion object {
            const val STOP_TIMEOUT_MS = 5_000L
        }
    }
