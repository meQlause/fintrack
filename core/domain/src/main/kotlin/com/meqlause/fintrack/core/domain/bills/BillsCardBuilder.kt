package com.meqlause.fintrack.core.domain.bills

import com.meqlause.fintrack.core.domain.credit.CreditAccount
import com.meqlause.fintrack.core.domain.credit.CreditAccountFactory
import com.meqlause.fintrack.core.domain.credit.InstallmentScheduler
import com.meqlause.fintrack.core.domain.credit.StatementCalculator
import com.meqlause.fintrack.core.domain.credit.StatementDates
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.Wallet
import java.time.LocalDate
import java.time.ZoneId

// FR-137, FR-146, BR-033, BR-034: the Tagihan rows and this period's instalments; pure, callers pass the dates
public object BillsCardBuilder {
    private val billOrder = compareBy<CreditBill>({ rankOf(it.statement.status) }, { it.statement.period.dueDate })

    /** FR-137: active credit wallets, most urgent first; FR-146: portions whose statement falls due inside [period]. */
    public fun build(
        wallets: List<Wallet>,
        transactions: List<Transaction>,
        plans: List<InstallmentPlan>,
        period: Period,
        today: LocalDate,
        zone: ZoneId,
    ): BillsCard {
        val accounts =
            wallets
                .filter { it.terms.credit != null && !it.status.isArchived }
                .map { wallet -> wallet to CreditAccountFactory.build(wallet, transactions, plans, zone) }
        val bills =
            accounts
                .map { (wallet, account) -> billOf(wallet, account, today) }
                .sortedWith(billOrder)
        val instalments = accounts.fold(Money(0L)) { total, (_, account) -> total + instalmentsDueIn(account, period) }
        return BillsCard(bills, instalments)
    }

    private fun billOf(
        wallet: Wallet,
        account: CreditAccount,
        today: LocalDate,
    ): CreditBill = CreditBill(wallet.walletId, wallet.profile.name, StatementCalculator.current(account, today))

    // FR-146: a portion counts when the statement that bills it has its due date inside the period
    private fun instalmentsDueIn(
        account: CreditAccount,
        period: Period,
    ): Money {
        val terms = account.terms
        return account.instalments.fold(Money(0L)) { total, plan ->
            InstallmentScheduler.schedule(plan).fold(total) { running, portion ->
                val due = StatementDates.dueDate(portion.statementMonth, terms.statementDay, terms.dueDay)
                if (due in period.startDate..period.endDate) running + portion.amount else running
            }
        }
    }

    // BR-034: overdue first, then due, paid and not yet issued
    private fun rankOf(status: StatementStatus): Int =
        when (status) {
            StatementStatus.OVERDUE -> 0
            StatementStatus.DUE -> 1
            StatementStatus.PAID -> 2
            StatementStatus.NOT_ISSUED -> 3
        }
}
