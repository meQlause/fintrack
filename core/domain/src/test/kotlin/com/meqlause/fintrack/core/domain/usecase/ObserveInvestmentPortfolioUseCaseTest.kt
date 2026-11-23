package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.investment.ActivityKind
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-173, FR-174: the Investasi tab lists active investment accounts, their totals, allocation and newest activity

/** FR-173, FR-174: observe investment portfolio use case tests. */
class ObserveInvestmentPortfolioUseCaseTest {
    private val wallets = InMemoryWalletRepository()
    private val transactions = InMemoryTransactionRepository()
    private val valuations = InMemoryValuationRepository()
    private val observe = ObserveInvestmentPortfolioUseCase(wallets, transactions, valuations)

    /** FR-173: archived wallets and wallets of other types never appear in the portfolio. */
    @Test
    fun givenArchivedAndPlainWalletsWhenObservedThenOnlyActiveInvestmentsAreListed() {
        runBlocking {
            wallets.insert(walletOf(WalletType.BANK, initial = 100L))
            wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.STOCK, archived = true))
            val fund = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.MUTUAL_FUND))
            val portfolio = observe().first()
            assertThat(portfolio.accounts.map { it.wallet.walletId }).containsExactly(fund)
        }
    }

    /** FR-173, FR-174: totals sum modal and value over accounts; allocation groups by kind, largest first. */
    @Test
    fun givenTwoAccountsWhenObservedThenTotalsAndAllocationAreSummed() {
        runBlocking {
            val bank = wallets.insert(walletOf(WalletType.BANK, initial = 5_000_000L))
            val fund = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.MUTUAL_FUND))
            val gold = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.GOLD))
            transactions.insert(transferRow(bank, fund, 1_000_000L, "2026-10-02T08:00:00Z"))
            transactions.insert(transferRow(bank, gold, 500_000L, "2026-10-03T08:00:00Z"))
            valuations.insert(valuationRow(fund, 1_200_000L, "2026-11-20T08:00:00Z"))
            val portfolio = observe().first()
            assertThat(portfolio.total.modal).isEqualTo(Money(1_500_000L))
            assertThat(portfolio.total.currentValue).isEqualTo(Money(1_700_000L))
            assertThat(portfolio.allocation.keys.toList()).containsExactly(InvestKind.MUTUAL_FUND, InvestKind.GOLD)
            assertThat(portfolio.allocation[InvestKind.MUTUAL_FUND]).isEqualTo(Money(1_200_000L))
        }
    }

    /** FR-173: an investment without a kind is grouped under OTHER in the allocation. */
    @Test
    fun givenWalletWithoutKindWhenObservedThenAllocationUsesOther() {
        runBlocking {
            wallets.insert(walletOf(WalletType.INVESTMENT, investKind = null, initial = 250_000L))
            val portfolio = observe().first()
            assertThat(portfolio.allocation).isEqualTo(mapOf(InvestKind.OTHER to Money(250_000L)))
        }
    }

    /** FR-173: the recent list keeps the newest ten activities across all accounts. */
    @Test
    fun givenMoreThanTenActivitiesWhenObservedThenOnlyTheNewestTenAreKept() {
        runBlocking {
            val bank = wallets.insert(walletOf(WalletType.BANK, initial = 99_000_000L))
            val fund = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.STOCK))
            (1..12).forEach { day ->
                transactions.insert(transferRow(bank, fund, 1_000L, "2026-11-%02dT08:00:00Z".format(day)))
            }
            val recent = observe().first().recent
            assertThat(recent).hasSize(10)
            assertThat(recent.first().occurredAt).isEqualTo(Instant.parse("2026-11-12T08:00:00Z"))
            assertThat(recent.first().kind).isEqualTo(ActivityKind.PUT_IN)
        }
    }
}
