package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** FR-140: in-memory instalment plans for the domain tests; core:testing cannot be used from this pure JVM module. */
internal class InMemoryInstallmentPlanRepository : InstallmentPlanRepository {
    private val rows = MutableStateFlow<List<InstallmentPlan>>(emptyList())
    private var lastId = 0L

    override fun observeAll(): Flow<List<InstallmentPlan>> = rows

    override suspend fun insert(plan: InstallmentPlan): InstallmentPlanId {
        lastId += 1
        val id = InstallmentPlanId(lastId)
        rows.value = rows.value + plan.copy(installmentPlanId = id)
        return id
    }

    override suspend fun update(plan: InstallmentPlan) {
        rows.value = rows.value.map { if (it.installmentPlanId == plan.installmentPlanId) plan else it }
    }

    override suspend fun delete(installmentPlanId: InstallmentPlanId) {
        rows.value = rows.value.filterNot { it.installmentPlanId == installmentPlanId }
    }
}
