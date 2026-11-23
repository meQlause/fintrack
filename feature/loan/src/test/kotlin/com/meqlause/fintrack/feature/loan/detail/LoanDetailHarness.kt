package com.meqlause.fintrack.feature.loan.detail

import androidx.lifecycle.SavedStateHandle
import com.meqlause.fintrack.core.domain.usecase.ChangeLoanDueDateUseCase
import com.meqlause.fintrack.core.domain.usecase.DeleteLoanUseCase
import com.meqlause.fintrack.core.domain.usecase.ObserveLoansUseCase
import com.meqlause.fintrack.core.domain.usecase.RecordRepaymentUseCase
import com.meqlause.fintrack.core.domain.usecase.WriteOffLoanUseCase
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.testing.repository.FakeLoanRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.flow.first
import java.time.Instant

// FR-152, S-17: the fakes and the ViewModel factory that the loan detail ViewModel tests share
internal class LoanDetailHarness {
    val fixedNow: Instant = Instant.parse("2026-11-23T07:05:00Z")
    val clock = FakeClock(fixedNow)
    val loans = FakeLoanRepository()
    val transactions = FakeTransactionRepository()
    val atomic = PassthroughAtomicRunner()

    fun viewModel(loanId: Long): LoanDetailViewModel =
        LoanDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("loanId" to loanId)),
            observeLoans = ObserveLoansUseCase(loans, transactions),
            wallets = FakeWalletRepository(transactions),
            commands =
                LoanCommands(
                    repaymentUseCase = RecordRepaymentUseCase(loans, transactions, atomic, clock),
                    writeOffUseCase = WriteOffLoanUseCase(loans, clock),
                    dueDateUseCase = ChangeLoanDueDateUseCase(loans, clock),
                    deleteUseCase = DeleteLoanUseCase(loans, transactions, atomic),
                ),
            clock = clock,
        )

    suspend fun insertLoan(): Long =
        loans
            .insert(
                Loan(
                    loanId = LoanId(0L),
                    agreement =
                        Agreement(
                            direction = LoanDirection.LENT,
                            personName = "Budi",
                            principal = Money(300_000L),
                            startDate = fixedNow,
                            dueDate = null,
                            note = "",
                        ),
                    status = LoanStatus.OPEN,
                    closedAt = null,
                    audit = Audit(fixedNow, fixedNow),
                ),
            ).value

    suspend fun loanOf(loanKey: Long): Loan? = loans.observeAll().first().firstOrNull { it.loanId.value == loanKey }
}
