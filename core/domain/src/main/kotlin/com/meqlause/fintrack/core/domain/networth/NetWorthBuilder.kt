package com.meqlause.fintrack.core.domain.networth

import com.meqlause.fintrack.core.domain.credit.CreditAccountFactory
import com.meqlause.fintrack.core.domain.credit.StatementCalculator
import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import java.time.LocalDate
import java.time.ZoneId

// BR-043, BR-053: builds the net worth of one moment; pure, callers pass the date and the investment total
internal object NetWorthBuilder {
    /** BR-043, BR-053: [investments] is the summed current value of the active investment accounts. */
    fun build(
        ledger: NetWorthLedger,
        loans: List<LoanEntry>,
        investments: Money,
        today: LocalDate,
        zone: ZoneId,
    ): NetWorth {
        val active = ledger.wallets.filterNot { it.status.isArchived }
        return NetWorth(
            saldo = saldoOf(active, ledger.moneyByWallet),
            investasi = investments,
            piutang = outstandingOf(loans, LoanDirection.LENT),
            utangKartu = cardDebtOf(active, ledger, today, zone),
            hutang = outstandingOf(loans, LoanDirection.BORROWED),
        )
    }

    // BR-043, BR-053: Saldo counts non-credit, non-investment wallets that are included in the total
    private fun saldoOf(
        active: List<Wallet>,
        balances: Map<WalletId, Money>,
    ): Money =
        active
            .filter { it.status.isIncludedInTotal && !it.isCredit() && it.kind.type != WalletType.INVESTMENT }
            .fold(Money(0L)) { sum, wallet -> sum + (balances[wallet.walletId] ?: wallet.terms.initialBalance) }

    // BR-043, BR-031: Utang kartu is the outstanding of every non-archived credit wallet on [today]
    private fun cardDebtOf(
        active: List<Wallet>,
        ledger: NetWorthLedger,
        today: LocalDate,
        zone: ZoneId,
    ): Money =
        active
            .filter { it.isCredit() && it.terms.credit != null }
            .fold(Money(0L)) { sum, wallet ->
                val account = CreditAccountFactory.build(wallet, ledger.transactions, ledger.plans, zone)
                sum + StatementCalculator.outstandingOn(account, today)
            }

    // BR-043, BR-041: Piutang and Hutang are the outstanding amounts of OPEN loans in one direction
    private fun outstandingOf(
        loans: List<LoanEntry>,
        direction: LoanDirection,
    ): Money =
        loans
            .filter { it.loan.status == LoanStatus.OPEN && it.loan.agreement.direction == direction }
            .fold(Money(0L)) { sum, entry -> sum + entry.loanBalance.outstanding }

    private fun Wallet.isCredit(): Boolean = kind.type == WalletType.CREDIT_CARD || kind.type == WalletType.PAYLATER
}
