package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.budget.BudgetAlert
import com.meqlause.fintrack.core.domain.budget.BudgetAlertRules
import com.meqlause.fintrack.core.domain.budget.BudgetCalendar
import com.meqlause.fintrack.core.domain.budget.BudgetSpending
import com.meqlause.fintrack.core.domain.budget.BudgetStatus
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.domain.repository.BudgetAlertLogRepository
import com.meqlause.fintrack.core.domain.repository.BudgetAlertNotifier
import com.meqlause.fintrack.core.domain.repository.BudgetRepository
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.model.BudgetAlertLog
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Transaction
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/** FR-073, BR-020, BR-021: alerts once per budget, period and threshold when spending crosses 80 or 100 percent. */
public class EvaluateBudgetAlertsUseCase
    @Inject
    constructor(
        private val budgets: BudgetRepository,
        private val transactions: TransactionRepository,
        private val categories: CategoryRepository,
        private val settings: SettingsRepository,
        private val alertLog: BudgetAlertLogRepository,
        private val notifier: BudgetAlertNotifier,
        private val clock: Clock,
    ) {
        /** FR-073: runs after any transaction change; repeated runs send nothing new (BR-021). */
        public suspend operator fun invoke() {
            val period =
                BudgetCalendar.currentPeriod(
                    clock,
                    settings
                        .observe()
                        .first()
                        .preferences.periodStartDay,
                )
            val txs = transactions.observeAll().first()
            val logs = alertLog.observeAll().first()
            val names = categories.observeAll().first().associate { it.categoryId to it.profile.name }
            budgets.observeAll().first().filter { it.isActive }.forEach { budget ->
                val status = BudgetStatus(budget, BudgetSpending.spentIn(budget, txs, period, clock.zone))
                checkBudget(status, period, logs, names)
            }
        }

        // FR-073: records every newly crossed threshold, then notifies once for the highest one
        private suspend fun checkBudget(
            status: BudgetStatus,
            period: Period,
            logs: List<BudgetAlertLog>,
            names: Map<CategoryId, String>,
        ) {
            val start = BudgetCalendar.startInstant(period, clock.zone)
            val budgetId = status.budget.budgetId
            val sent = logs.filter { it.budgetId == budgetId && it.periodStart == start }.map { it.threshold }.toSet()
            val crossings = BudgetAlertRules.unsentCrossings(status.percent, sent)
            if (crossings.isEmpty()) return
            val now = Instant.now(clock)
            crossings.forEach { alertLog.insert(BudgetAlertLog(budgetId, start, it, now)) }
            val name = status.budget.categoryId?.let { names[it] }
            notifier.show(BudgetAlert(budgetId, name, crossings.last(), status.spent, status.budget.limitAmount))
        }
    }
