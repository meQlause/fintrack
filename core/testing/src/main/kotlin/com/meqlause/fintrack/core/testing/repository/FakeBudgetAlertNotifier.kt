package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.domain.budget.BudgetAlert
import com.meqlause.fintrack.core.domain.port.BudgetAlertNotifier

/** FR-073: records every alert instead of posting a notification, so tests can count them. */
public class FakeBudgetAlertNotifier : BudgetAlertNotifier {
    private val shown = mutableListOf<BudgetAlert>()

    /** FR-073: every alert shown so far, in order. */
    public val alerts: List<BudgetAlert>
        get() = shown.toList()

    override fun show(alert: BudgetAlert) {
        shown += alert
    }
}
