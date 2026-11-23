package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.budget.BudgetCalendar
import com.meqlause.fintrack.core.domain.budget.BudgetSpending
import com.meqlause.fintrack.core.domain.budget.BudgetStatus
import com.meqlause.fintrack.core.domain.repository.BudgetRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Clock
import javax.inject.Inject

/** FR-071, FR-072, BR-020: active budgets with what each has spent in the current period (BR-010). */
public class ObserveBudgetStatusUseCase
    @Inject
    constructor(
        private val budgets: BudgetRepository,
        private val transactions: TransactionRepository,
        private val settings: SettingsRepository,
        private val clock: Clock,
    ) {
        /** FR-071: re-computed whenever a budget, a transaction or the period start day changes. */
        public operator fun invoke(): Flow<List<BudgetStatus>> =
            combine(budgets.observeAll(), transactions.observeAll(), settings.observe()) { all, txs, prefs ->
                val period = BudgetCalendar.currentPeriod(clock, prefs.preferences.periodStartDay)
                all
                    .filter { it.isActive }
                    .map { BudgetStatus(it, BudgetSpending.spentIn(it, txs, period, clock.zone)) }
            }
    }
