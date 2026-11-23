package com.meqlause.fintrack.core.domain.reminder

import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletType
import java.time.LocalDate
import java.time.YearMonth

/**
 * FR-176, BR-055: the monthly update reminder, on day 1, only when the setting is on and an
 * investment wallet exists
 */
public object InvestmentReminders {
    private const val FIRST_DAY = 1

    /** BR-055: at most one reminder per month, posted on the 1st. */
    public fun collect(
        wallets: List<Wallet>,
        enabled: Boolean,
        today: LocalDate,
    ): List<InvestmentUpdateReminder> {
        val hasInvestment = wallets.any { it.kind.type == WalletType.INVESTMENT && !it.status.isArchived }
        val isDue = enabled && hasInvestment && today.dayOfMonth == FIRST_DAY
        return if (isDue) listOf(InvestmentUpdateReminder(YearMonth.from(today))) else emptyList()
    }
}
