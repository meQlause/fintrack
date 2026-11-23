package com.meqlause.fintrack.core.domain.reminder

import com.meqlause.fintrack.core.domain.credit.CreditAccountFactory
import com.meqlause.fintrack.core.domain.credit.StatementCalculator
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.Wallet
import java.time.LocalDate
import java.time.ZoneId

// FR-135, BR-045: card statements that are due in a lead day with remainingDue above zero
public object CardBillReminders {
    /** FR-135: reminders for the current statement of every active credit wallet. */
    public fun of(
        wallets: List<Wallet>,
        transactions: List<Transaction>,
        plans: List<InstallmentPlan>,
        today: LocalDate,
        zone: ZoneId,
    ): List<CardBillReminder> =
        wallets
            .filter { it.terms.credit != null && !it.status.isArchived }
            .mapNotNull { wallet -> reminderOf(wallet, transactions, plans, today, zone) }

    private fun reminderOf(
        wallet: Wallet,
        transactions: List<Transaction>,
        plans: List<InstallmentPlan>,
        today: LocalDate,
        zone: ZoneId,
    ): CardBillReminder? {
        val statement =
            StatementCalculator.current(
                CreditAccountFactory.build(wallet, transactions, plans, zone),
                today,
            )
        val due = statement.period.dueDate
        val lead = ReminderRules.leadOf(due, today) ?: return null
        val remaining = statement.breakdown.remainingDue
        if (remaining.rupiah <= 0L) return null
        return CardBillReminder(wallet.walletId, wallet.profile.name, remaining, due, lead)
    }
}
