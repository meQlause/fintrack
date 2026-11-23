package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.investment.InvestmentAccountBuilder
import com.meqlause.fintrack.core.domain.investment.InvestmentActivityBuilder
import com.meqlause.fintrack.core.domain.investment.InvestmentDetail
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.ValuationRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

// FR-173, S-20: one investment account with its timeline, recomputed on every change
public class ObserveInvestmentDetailUseCase
    @Inject
    constructor(
        private val wallets: WalletRepository,
        private val transactions: TransactionRepository,
        private val valuations: ValuationRepository,
    ) {
        /** FR-173: null when the wallet is missing or is not an active investment. */
        public operator fun invoke(walletId: WalletId): Flow<InvestmentDetail?> =
            combine(wallets.observeAll(), transactions.observeAll(), valuations.observeAll()) { all, rows, values ->
                val wallet = all.firstOrNull { it.walletId == walletId }
                if (wallet == null || !InvestmentAccountBuilder.isActiveInvestment(wallet)) {
                    null
                } else {
                    InvestmentDetail(
                        account = InvestmentAccountBuilder.accountOf(wallet, rows, values),
                        activity = InvestmentActivityBuilder.activityOf(walletId, rows, values),
                    )
                }
            }
    }
