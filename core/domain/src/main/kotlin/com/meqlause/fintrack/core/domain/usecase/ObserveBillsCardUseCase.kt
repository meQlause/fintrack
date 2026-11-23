package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.bills.BillsCard
import com.meqlause.fintrack.core.domain.bills.BillsCardBuilder
import com.meqlause.fintrack.core.domain.credit.CreditSnapshot
import com.meqlause.fintrack.core.domain.period.DayContext
import com.meqlause.fintrack.core.domain.period.PeriodCalculator
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

/** FR-137, FR-146: the Tagihan card, recomputed when a wallet, a transaction, a plan or the period start day changes */
public class ObserveBillsCardUseCase
    @Inject
    constructor(
        private val wallets: WalletRepository,
        private val transactions: TransactionRepository,
        private val plans: InstallmentPlanRepository,
        private val settings: SettingsRepository,
        private val clock: Clock,
    ) {
        /** FR-137: the credit bills and the instalments due in the current period. */
        public operator fun invoke(): Flow<BillsCard> =
            combine(wallets.observeAll(), transactions.observeAll(), plans.observeAll(), settings.observe()) {
                all,
                txs,
                planList,
                prefs,
                ->
                val today = LocalDate.now(clock)
                val period = PeriodCalculator.periodContaining(today, prefs.preferences.periodStartDay)
                val snapshot = CreditSnapshot(all, txs, planList)
                BillsCardBuilder.build(snapshot, period, DayContext(today, clock.zone))
            }
    }
