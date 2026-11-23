package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.investment.InvestmentAccountBuilder
import com.meqlause.fintrack.core.domain.investment.InvestmentActivityBuilder
import com.meqlause.fintrack.core.domain.investment.InvestmentPortfolio
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.ValuationRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

// FR-173, FR-174: the Investasi tab data, recomputed whenever a wallet, transfer or valuation changes
public class ObserveInvestmentPortfolioUseCase
    @Inject
    constructor(
        private val wallets: WalletRepository,
        private val transactions: TransactionRepository,
        private val valuations: ValuationRepository,
    ) {
        /** FR-173: the portfolio with the newest [RECENT_LIMIT] activities across all accounts. */
        public operator fun invoke(): Flow<InvestmentPortfolio> =
            combine(wallets.observeAll(), transactions.observeAll(), valuations.observeAll()) { all, rows, values ->
                val accounts = InvestmentAccountBuilder.accountsOf(all, rows, values)
                val recent =
                    accounts
                        .flatMap { InvestmentActivityBuilder.activityOf(it.wallet.walletId, rows, values) }
                        .sortedByDescending { it.occurredAt }
                        .take(RECENT_LIMIT)
                InvestmentPortfolio(accounts = accounts, recent = recent)
            }

        private companion object {
            const val RECENT_LIMIT = 10
        }
    }
