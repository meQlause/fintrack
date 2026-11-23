package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.mapper.toEntity
import com.meqlause.fintrack.core.data.mapper.toLog
import com.meqlause.fintrack.core.database.dao.BudgetAlertLogDao
import com.meqlause.fintrack.core.domain.repository.BudgetAlertLogRepository
import com.meqlause.fintrack.core.model.BudgetAlertLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** DOC-03, ADR-007, FR-073: sent budget alerts in Room; the composite key allows one alert per threshold and period. */
public class LocalBudgetAlertLogRepository(
    private val alertLogDao: BudgetAlertLogDao,
) : BudgetAlertLogRepository {
    override fun observeAll(): Flow<List<BudgetAlertLog>> =
        alertLogDao.observeAll().map { rows -> rows.map { it.toLog() } }

    override suspend fun insert(alertLog: BudgetAlertLog) = alertLogDao.insert(alertLog.toEntity())
}
