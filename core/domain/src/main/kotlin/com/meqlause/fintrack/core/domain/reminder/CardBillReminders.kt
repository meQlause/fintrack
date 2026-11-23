package com.meqlause.fintrack.core.domain.reminder

import com.meqlause.fintrack.core.domain.credit.CreditAccountFactory
import com.meqlause.fintrack.core.domain.credit.CreditSnapshot
import com.meqlause.fintrack.core.domain.credit.StatementCalculator
import com.meqlause.fintrack.core.domain.period.DayContext
import com.meqlause.fintrack.core.model.Wallet

/** FR-135, BR-045: card statements that are due in a lead day with remainingDue above zero */
public object CardBillReminders {
    /** FR-135: reminders for the current statement of every active credit wallet. */
    public fun collect(
        snapshot: CreditSnapshot,
        dayContext: DayContext,
    ): List<CardBillReminder> =
        snapshot.wallets
            .filter { it.terms.credit != null && !it.status.isArchived }
            .mapNotNull { wallet -> reminderOf(wallet, snapshot, dayContext) }

    private fun reminderOf(
        wallet: Wallet,
        snapshot: CreditSnapshot,
        dayContext: DayContext,
    ): CardBillReminder? {
        val account = CreditAccountFactory.build(wallet, snapshot.transactions, snapshot.plans, dayContext.zone)
        val statement = StatementCalculator.current(account, dayContext.today)
        val dueDate = statement.period.dueDate
        val lead = ReminderRules.leadOf(dueDate, dayContext.today)
        val remaining = statement.breakdown.remainingDue
        if (lead == null || remaining.rupiah <= 0L) return null
        return CardBillReminder(wallet.walletId, wallet.profile.name, remaining, dueDate, lead)
    }
}
