package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import kotlinx.serialization.Serializable

// DOC-11: one loan row in the backup; the direction is kept as agreed at opening
@Serializable
internal data class LoanDto(
    val loanId: Long,
    val agreement: AgreementDto,
    val status: String,
    val closedAt: String?,
    val audit: AuditDto,
) {
    fun toModel(): Loan =
        Loan(
            loanId = LoanId(loanId),
            agreement = agreement.toModel(),
            status = BackupValues.enumNamed<LoanStatus>(status),
            closedAt = closedAt?.let(BackupValues::instant),
            audit = audit.toModel(),
        )

    companion object {
        fun from(loan: Loan): LoanDto =
            LoanDto(
                loanId = loan.loanId.value,
                agreement = AgreementDto.from(loan.agreement),
                status = loan.status.name,
                closedAt = loan.closedAt?.toString(),
                audit = AuditDto.from(loan.audit),
            )
    }

    @Serializable
    internal data class AgreementDto(
        val direction: String,
        val personName: String,
        val principal: Long,
        val startDate: String,
        val dueDate: String?,
        val note: String,
    ) {
        fun toModel(): Agreement =
            Agreement(
                direction = BackupValues.enumNamed<LoanDirection>(direction),
                personName = personName,
                principal = Money(principal),
                startDate = BackupValues.instant(startDate),
                dueDate = dueDate?.let(BackupValues::instant),
                note = note,
            )

        companion object {
            fun from(agreement: Agreement): AgreementDto =
                AgreementDto(
                    agreement.direction.name,
                    agreement.personName,
                    agreement.principal.rupiah,
                    agreement.startDate.toString(),
                    agreement.dueDate?.toString(),
                    agreement.note,
                )
        }
    }
}
