package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Purchase
import com.meqlause.fintrack.core.model.Schedule
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.serialization.Serializable

// DOC-11: one instalment plan row in the backup; portions are recomputed, never stored
@Serializable
internal data class InstallmentPlanDto(
    val installmentPlanId: Long,
    val purchase: PurchaseDto,
    val schedule: ScheduleDto,
    val status: String,
    val audit: AuditDto,
) {
    fun toModel(): InstallmentPlan =
        InstallmentPlan(
            installmentPlanId = InstallmentPlanId(installmentPlanId),
            purchase = purchase.toModel(),
            schedule = schedule.toModel(),
            status = BackupValues.enumNamed<InstallmentStatus>(status),
            audit = audit.toModel(),
        )

    companion object {
        fun from(plan: InstallmentPlan): InstallmentPlanDto =
            InstallmentPlanDto(
                installmentPlanId = plan.installmentPlanId.value,
                purchase =
                    PurchaseDto(
                        plan.purchase.transactionId.value,
                        plan.purchase.walletId.value,
                        plan.purchase.title,
                        plan.purchase.principal.rupiah,
                        plan.purchase.adminFeeTransactionId?.value,
                    ),
                schedule =
                    ScheduleDto(
                        plan.schedule.tenorMonths,
                        plan.schedule.firstStatementMonth.toString(),
                        plan.schedule.earlyPayoffMonth?.toString(),
                    ),
                status = plan.status.name,
                audit = AuditDto.from(plan.audit),
            )
    }

    @Serializable
    internal data class PurchaseDto(
        val transactionId: Long,
        val walletId: Long,
        val title: String,
        val principal: Long,
        val adminFeeTransactionId: Long?,
    ) {
        fun toModel(): Purchase =
            Purchase(
                transactionId = TransactionId(transactionId),
                walletId = WalletId(walletId),
                title = title,
                principal = Money(principal),
                adminFeeTransactionId = adminFeeTransactionId?.let(::TransactionId),
            )
    }

    @Serializable
    internal data class ScheduleDto(
        val tenorMonths: Int,
        val firstStatementMonth: String,
        val earlyPayoffMonth: String?,
    ) {
        fun toModel(): Schedule =
            Schedule(
                tenorMonths = tenorMonths,
                firstStatementMonth = BackupValues.yearMonth(firstStatementMonth),
                earlyPayoffMonth = earlyPayoffMonth?.let(BackupValues::yearMonth),
            )
    }
}
