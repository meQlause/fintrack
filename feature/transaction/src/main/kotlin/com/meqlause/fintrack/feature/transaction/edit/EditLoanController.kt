package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.loan.LoanDraft
import com.meqlause.fintrack.core.domain.loan.LoanSchedule
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.usecase.SaveLoanUseCase
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

private const val MAX_PERSON_LENGTH = 40

// FR-150, FR-151, FR-158, S-04: the Pinjaman tab; a loan is opened on the sheet instead of a plain transaction
internal class EditLoanController
    @Inject
    constructor(
        private val saveLoan: SaveLoanUseCase,
        private val clock: Clock,
    ) {
        /** FR-150: the loan and its opening movement are stored together; rule errors come back as on other tabs. */
        suspend fun store(state: EditTransactionUiState): SaveTransactionResult {
            val loan = requireNotNull(state.loan) { "the Pinjaman tab is open" }
            val draft =
                LoanDraft(
                    direction = loan.direction,
                    personName = loan.personName,
                    principal = state.form.amount,
                    walletId = state.form.walletId,
                    schedule = LoanSchedule(startDate = state.record.occurredAt, dueDate = loan.dueDate),
                    note = state.form.note,
                )
            return saveLoan(draft)
        }

        // S-04: one control of the Pinjaman block changed; the errors shown so far no longer apply
        fun reduce(
            state: EditTransactionUiState,
            change: LoanChange,
        ): EditTransactionUiState =
            when (change) {
                LoanChange.TabSelected -> {
                    state
                        .withExtras { copy(loan = loan ?: LoanForm()) }
                        .withStatus { copy(openSheet = null, errors = emptySet()) }
                }

                is LoanChange.DirectionPicked -> {
                    state.withLoan { it.copy(direction = change.direction) }
                }

                is LoanChange.PersonChanged -> {
                    state.withLoan {
                        it.copy(
                            personName = change.name.take(MAX_PERSON_LENGTH),
                        )
                    }
                }

                is LoanChange.DuePicked -> {
                    dueChosen(state, change)
                }
            }

        // FR-158: the option counts from today; "Tidak ada" clears the date
        private fun dueChosen(
            state: EditTransactionUiState,
            change: LoanChange.DuePicked,
        ): EditTransactionUiState {
            val dueOn = change.option.dueFrom(LocalDate.now(clock))
            return state.withLoan { it.copy(dueDate = dueOn) }.withStatus { copy(openSheet = null) }
        }

        private fun EditTransactionUiState.withLoan(transform: (LoanForm) -> LoanForm): EditTransactionUiState =
            withExtras { copy(loan = transform(loan ?: LoanForm())) }.withStatus { copy(errors = emptySet()) }
    }
