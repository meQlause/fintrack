package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.investment.ActivityKind
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

// FR-173: the investment detail screen shows one active investment account with its timeline

/** FR-173: observe investment detail use case tests. */
class ObserveInvestmentDetailUseCaseTest {
    private val wallets = InMemoryWalletRepository()
    private val transactions = InMemoryTransactionRepository()
    private val valuations = InMemoryValuationRepository()
    private val observe = ObserveInvestmentDetailUseCase(wallets, transactions, valuations)

    /** FR-173: a missing wallet has no detail. */
    @Test
    fun givenUnknownWalletWhenObservedThenDetailIsNull() {
        runBlocking {
            assertThat(observe(WalletId(99L)).first()).isNull()
        }
    }

    /** FR-173: a plain or archived wallet is not an investment and has no detail. */
    @Test
    fun givenPlainOrArchivedWalletWhenObservedThenDetailIsNull() {
        runBlocking {
            val bank = wallets.insert(walletOf(WalletType.BANK))
            val archived = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.GOLD, archived = true))
            assertThat(observe(bank).first()).isNull()
            assertThat(observe(archived).first()).isNull()
        }
    }

    /** FR-173: an active investment shows its valued account and its activity, newest first. */
    @Test
    fun givenActiveInvestmentWhenObservedThenAccountAndActivityAreReturned() {
        runBlocking {
            val bank = wallets.insert(walletOf(WalletType.BANK))
            val fund = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.MUTUAL_FUND))
            transactions.insert(transferRow(bank, fund, 1_000_000L, "2026-10-02T08:00:00Z"))
            valuations.insert(valuationRow(fund, 1_100_000L, "2026-11-20T08:00:00Z"))
            val detail = observe(fund).first()
            assertThat(detail?.account?.valuation?.currentValue).isEqualTo(Money(1_100_000L))
            val kinds = detail?.activity?.map { it.kind }
            assertThat(kinds).isEqualTo(listOf(ActivityKind.VALUATION, ActivityKind.PUT_IN))
        }
    }
}
