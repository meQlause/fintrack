package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.domain.repository.BudgetAlertLogRepository
import com.meqlause.fintrack.core.domain.repository.BudgetRepository
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import javax.inject.Inject

/** The stores that budget alerts read budgets, spending and categories from, and log the alerts they sent into. */
public class BudgetAlertSources
    @Inject
    constructor(
        public val budgets: BudgetRepository,
        public val transactions: TransactionRepository,
        public val categories: CategoryRepository,
        public val alertLog: BudgetAlertLogRepository,
    )
