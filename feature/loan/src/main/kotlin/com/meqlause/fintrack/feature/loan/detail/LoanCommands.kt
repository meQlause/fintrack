package com.meqlause.fintrack.feature.loan.detail

import com.meqlause.fintrack.core.domain.usecase.ChangeLoanDueDateUseCase
import com.meqlause.fintrack.core.domain.usecase.DeleteLoanUseCase
import com.meqlause.fintrack.core.domain.usecase.RecordRepaymentUseCase
import com.meqlause.fintrack.core.domain.usecase.WriteOffLoanUseCase
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import java.time.LocalDate
import javax.inject.Inject

// FR-152, FR-155, FR-158: the use cases that change one loan from the detail screen
internal class LoanCommands
    @Inject
    constructor(
        private val repaymentUseCase: RecordRepaymentUseCase,
        private val writeOffUseCase: WriteOffLoanUseCase,
        private val dueDateUseCase: ChangeLoanDueDateUseCase,
        private val deleteUseCase: DeleteLoanUseCase,
    ) {
        // FR-152: the repayment is checked and stored by the use case
        suspend fun recordRepayment(
            loanId: LoanId,
            amount: Money,
            walletId: WalletId?,
        ) = repaymentUseCase(loanId, amount, walletId)

        // FR-155: the piutang is closed as written off
        suspend fun writeOff(loanId: LoanId) = writeOffUseCase(loanId)

        // FR-158: the new due date replaces the old one
        suspend fun changeDueDate(
            loanId: LoanId,
            dueDate: LocalDate?,
        ) = dueDateUseCase(loanId, dueDate)

        // BR-046: the loan and its movements are removed
        suspend fun delete(loanId: LoanId) = deleteUseCase(loanId)
    }
