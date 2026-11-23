package com.meqlause.fintrack.core.domain.bills

import com.meqlause.fintrack.core.domain.credit.CreditAccount
import com.meqlause.fintrack.core.domain.credit.CreditAccountFactory
import com.meqlause.fintrack.core.domain.credit.CreditSnapshot
import com.meqlause.fintrack.core.domain.credit.InstallmentScheduler
import com.meqlause.fintrack.core.domain.credit.StatementCalculator
import com.meqlause.fintrack.core.domain.credit.StatementDates
import com.meqlause.fintrack.core.domain.period.DayContext
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.Wallet
import java.time.LocalDate
import java.time.ZoneId

/** FR-137, FR-146, BR-033, BR-034: the Tagihan rows and this period's instalments; pure, callers pass the dates */
public object BillsCardBuilder {
    private val billOrder = compareBy<CreditBill>({ rankOf(it.statement.status) }, { it.statement.period.dueDate })
    private const val NOT_ISSUED_RANK = 3

    /** FR-137: active credit wallets, most urgent first; FR-146: portions whose statement falls due inside [period]. */
    public fun build(
        snapshot: CreditSnapshot,
        period: Period,
        dayContext: DayContext,
    ): BillsCard {
        val accounts =
            snapshot.wallets
                .filter { it.terms.credit != null && !it.status.isArchived }
                .map { wallet -> wallet to accountOf(wallet, snapshot, dayContext.zone) }
        val bills =
            accounts
                .map { (wallet, account) -> billOf(wallet, account, dayContext.today) }
                .sortedWith(billOrder)
        val instalments = accounts.fold(Money(0L)) { total, (_, account) -> total + instalmentsDueIn(account, period) }
        return BillsCard(bills, instalments)
    }

    private fun accountOf(
        wallet: Wallet,
        snapshot: CreditSnapshot,
        zone: ZoneId,
    ): CreditAccount = CreditAccountFactory.build(wallet, snapshot.transactions, snapshot.plans, zone)

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
                val dueDate = StatementDates.dueDate(portion.statementMonth, terms.statementDay, terms.dueDay)
                if (dueDate in period.startDate..period.endDate) running + portion.amount else running
            }
        }
    }

    // BR-034: overdue first, then due, paid and not yet issued
    private fun rankOf(status: StatementStatus): Int =
        when (status) {
            StatementStatus.OVERDUE -> 0
            StatementStatus.DUE -> 1
            StatementStatus.PAID -> 2
            StatementStatus.NOT_ISSUED -> NOT_ISSUED_RANK
        }
}
