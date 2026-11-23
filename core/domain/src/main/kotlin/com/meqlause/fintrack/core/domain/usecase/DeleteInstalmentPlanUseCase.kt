package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.port.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.model.InstallmentPlanId
import javax.inject.Inject

// FR-145: deleting a plan also deletes its purchase and admin fee, so no orphan transaction is left behind
public class DeleteInstalmentPlanUseCase
    @Inject
    constructor(
        private val plans: InstallmentPlanRepository,
        private val transactions: TransactionRepository,
        private val atomic: AtomicRunner,
    ) {
        /** FR-145, BR-011: removes the purchase, its admin fee and the plan together; unknown plans are ignored. */
        public suspend operator fun invoke(installmentPlanId: InstallmentPlanId) {
            val plan = plans.findPlan(installmentPlanId) ?: return
            atomic.atomically {
                plan.purchase.adminFeeTransactionId?.let { transactions.delete(it) }
                transactions.delete(plan.purchase.transactionId)
                plans.delete(installmentPlanId)
            }
        }
    }
