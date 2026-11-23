package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import kotlinx.coroutines.flow.Flow

/** DOC-03, FR-140: access to instalment plans on credit wallets (BR-035 to BR-037). */
public interface InstallmentPlanRepository {
    /** DOC-02: every installment plan in creation order. */
    public fun observeAll(): Flow<List<InstallmentPlan>>

    /** FR-140: stores a plan and returns its generated id; the installmentPlanId of [plan] is ignored. */
    public suspend fun insert(plan: InstallmentPlan): InstallmentPlanId

    /** FR-144: stores the changed schedule or status of a plan; the installmentPlanId of [plan] selects the row. */
    public suspend fun update(plan: InstallmentPlan)

    /** FR-145: removes the plan with the given id; an unknown id is ignored. */
    public suspend fun delete(installmentPlanId: InstallmentPlanId)
}
