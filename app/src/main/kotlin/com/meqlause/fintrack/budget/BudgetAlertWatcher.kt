package com.meqlause.fintrack.budget

import com.meqlause.fintrack.core.common.coroutines.ApplicationScope
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.usecase.EvaluateBudgetAlertsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// FR-073: re-checks budgets after every transaction change, so every save path (app, quick entry, inbox) alerts
@Singleton
class BudgetAlertWatcher
    @Inject
    constructor(
        private val transactions: TransactionRepository,
        private val evaluate: EvaluateBudgetAlertsUseCase,
        @param:ApplicationScope private val scope: CoroutineScope,
    ) {
        /** FR-073: starts once per process; repeated evaluations send nothing new (BR-021). */
        fun start() {
            scope.launch { transactions.observeAll().collect { evaluate() } }
        }
    }
