package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toInstallmentPlan
import com.meqlause.fintrack.core.database.dao.InstallmentPlanDao
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-140: instalment plans in Room; portions are computed, never stored (BR-035, BR-036). */
public class LocalInstallmentPlanRepository(
    private val installmentPlanDao: InstallmentPlanDao,
) : InstallmentPlanRepository {
    override fun observeAll(): Flow<List<InstallmentPlan>> =
        installmentPlanDao.observeAll().map { rows -> rows.map { it.toInstallmentPlan() } }

    override suspend fun insert(plan: InstallmentPlan): InstallmentPlanId =
        InstallmentPlanId(installmentPlanDao.insert(plan.toEntity()))

    override suspend fun update(plan: InstallmentPlan) {
        installmentPlanDao.update(plan.toEntity())
    }

    override suspend fun delete(installmentPlanId: InstallmentPlanId) {
        installmentPlanDao.delete(installmentPlanId.value)
    }
}
