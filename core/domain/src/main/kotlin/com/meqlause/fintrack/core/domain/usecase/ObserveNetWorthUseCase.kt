package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.domain.networth.NetWorthBuilder
import com.meqlause.fintrack.core.domain.networth.NetWorthLedger
import com.meqlause.fintrack.core.domain.period.DayContext
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

/** BR-043, BR-053: Kekayaan bersih for Dompet and the Ringkasan drawer, recomputed when any source changes */
public class ObserveNetWorthUseCase
    @Inject
    constructor(
        private val wallets: WalletRepository,
        private val transactions: TransactionRepository,
        private val plans: InstallmentPlanRepository,
        private val loans: ObserveLoansUseCase,
        private val investments: ObserveInvestmentPortfolioUseCase,
        private val clock: Clock,
    ) {
        /** BR-043, BR-053: the parts of Kekayaan bersih for the current date. */
        public operator fun invoke(): Flow<NetWorth> {
            val ledger =
                combine(
                    wallets.observeAll(),
                    wallets.observeBalances(),
                    transactions.observeAll(),
                    plans.observeAll(),
                    ::NetWorthLedger,
                )
            return combine(ledger, loans(), investments()) { data, loanRows, portfolio ->
                NetWorthBuilder.build(
                    ledger = data,
                    loans = loanRows,
                    investments = portfolio.total.currentValue,
                    day = DayContext(LocalDate.now(clock), clock.zone),
                )
            }
        }
    }
