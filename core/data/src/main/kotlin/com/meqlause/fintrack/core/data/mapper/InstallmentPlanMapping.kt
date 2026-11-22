package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.InstallmentPlanEntity
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Purchase
import com.meqlause.fintrack.core.model.Schedule
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId
import java.time.YearMonth

// DOC-02, FR-140: maps an instalment plan row to the domain model; statement months are YYYY-MM strings
internal fun InstallmentPlanEntity.toInstallmentPlan(): InstallmentPlan =
    InstallmentPlan(
        installmentPlanId = InstallmentPlanId(installmentPlanId),
        purchase =
            Purchase(
                transactionId = TransactionId(purchase.transactionId),
                walletId = WalletId(purchase.walletId),
                title = purchase.title,
                principal = Money(purchase.principal),
                adminFeeTransactionId = adminFeeTransactionId?.let(::TransactionId),
            ),
        schedule =
            Schedule(
                tenorMonths = schedule.tenorMonths,
                firstStatementMonth = YearMonth.parse(schedule.firstStatementMonth),
                earlyPayoffMonth = schedule.earlyPayoffMonth?.let { YearMonth.parse(it) },
            ),
        status = status,
        audit = audit.toAudit(),
    )

// DOC-02, FR-140: maps an instalment plan to its row
internal fun InstallmentPlan.toEntity(): InstallmentPlanEntity =
    InstallmentPlanEntity(
        installmentPlanId = installmentPlanId.value,
        purchase =
            InstallmentPlanEntity.Purchase(
                transactionId = purchase.transactionId.value,
                walletId = purchase.walletId.value,
                title = purchase.title,
                principal = purchase.principal.rupiah,
            ),
        schedule =
            InstallmentPlanEntity.Schedule(
                tenorMonths = schedule.tenorMonths,
                firstStatementMonth = schedule.firstStatementMonth.toString(),
                earlyPayoffMonth = schedule.earlyPayoffMonth?.toString(),
            ),
        adminFeeTransactionId = purchase.adminFeeTransactionId?.value,
        status = status,
        audit = audit.toColumns(),
    )
