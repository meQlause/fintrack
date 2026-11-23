package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.investment.InvestmentAccountBuilder
import com.meqlause.fintrack.core.domain.port.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.ValuationRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

// FR-172, BR-054: records a manual value of an investment; a value dated today replaces the earlier one of that day
public class SaveValuationUseCase
    @Inject
    constructor(
        private val wallets: WalletRepository,
        private val valuations: ValuationRepository,
        private val atomic: AtomicRunner,
    ) {
        /** FR-172: returns false when the wallet is not an active investment or the value is negative. */
        public suspend operator fun invoke(
            walletId: WalletId,
            value: Money,
            currentTime: Instant,
            zone: ZoneId,
        ): Boolean {
            val wallet = wallets.observeAll().first().firstOrNull { it.walletId == walletId } ?: return false
            if (!InvestmentAccountBuilder.isActiveInvestment(wallet) || value.rupiah < 0L) return false
            val day = currentTime.atZone(zone).toLocalDate()
            val sameDay =
                valuations.observeAll().first().filter {
                    it.walletId == walletId && it.valuedAt.atZone(zone).toLocalDate() == day
                }
            atomic.atomically {
                sameDay.forEach { valuations.delete(it.valuationId) }
                valuations.insert(ValuationEntry(ValuationEntryId(0L), walletId, value, currentTime, currentTime))
            }
            return true
        }
    }
