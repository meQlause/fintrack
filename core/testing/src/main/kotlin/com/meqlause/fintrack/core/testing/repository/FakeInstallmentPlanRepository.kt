package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import kotlinx.coroutines.flow.Flow

/** ADR-007, FR-140: in-memory instalment plans; each purchase transaction has at most one plan. */
public class FakeInstallmentPlanRepository : InstallmentPlanRepository {
    private val store =
        RowStore<InstallmentPlan>(
            idOf = { it.installmentPlanId.value },
            withId = { plan, newId -> plan.copy(installmentPlanId = InstallmentPlanId(newId)) },
            order = compareBy { it.installmentPlanId.value },
        )

    override fun observeAll(): Flow<List<InstallmentPlan>> = store.observe()

    override suspend fun insert(plan: InstallmentPlan): InstallmentPlanId =
        InstallmentPlanId(
            store.append(plan) { existing -> existing.purchase.transactionId == plan.purchase.transactionId },
        )

    override suspend fun update(plan: InstallmentPlan) {
        store.replace(plan)
    }

    override suspend fun delete(installmentPlanId: InstallmentPlanId) {
        store.remove(installmentPlanId.value)
    }
}
