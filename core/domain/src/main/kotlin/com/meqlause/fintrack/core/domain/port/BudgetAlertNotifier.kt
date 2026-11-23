package com.meqlause.fintrack.core.domain.repository

import com.meqlause.fintrack.core.domain.budget.BudgetAlert

/** DOC-03, FR-073: shows a budget alert on the device; implemented by core:notification. */
public interface BudgetAlertNotifier {
    /** FR-073: posts one local notification for [alert]; nothing leaves the device. */
    public fun show(alert: BudgetAlert)
}
