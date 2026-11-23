package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.domain.port.BudgetAlertNotifier

// FR-073: records every alert instead of posting it
internal class RecordingBudgetAlertNotifier : BudgetAlertNotifier {
    val alerts = mutableListOf<BudgetAlert>()

    override fun show(alert: BudgetAlert) {
        alerts += alert
    }
}
