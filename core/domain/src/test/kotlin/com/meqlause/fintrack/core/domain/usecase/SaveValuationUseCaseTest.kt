package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId

// FR-172, BR-054: a manual value of an investment; a value dated today replaces the earlier one of that day

/** FR-172, BR-054: save valuation use case tests. */
class SaveValuationUseCaseTest {
    private val zone = ZoneId.of("Asia/Jakarta")
    private val wallets = InMemoryWalletRepository()
    private val valuations = InMemoryValuationRepository()
    private val save = SaveValuationUseCase(wallets, valuations, ImmediateAtomicRunner())

    /** BR-054: a missing wallet is refused and nothing is stored. */
    @Test
    fun givenUnknownWalletWhenSavedThenRefusedAndNothingStored() {
        runBlocking {
            assertThat(save(WalletId(7L), Money(100L), Instant.parse("2026-11-20T02:00:00Z"), zone)).isFalse()
            assertThat(valuations.observeAll().first()).isEmpty()
        }
    }

    /** FR-172: a bank wallet, an archived investment and a negative value are all refused. */
    @Test
    fun givenNonInvestmentArchivedOrNegativeWhenSavedThenRefused() {
        runBlocking {
            val bank = wallets.insert(walletOf(WalletType.BANK))
            val archived = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.GOLD, archived = true))
            val fund = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.MUTUAL_FUND))
            val moment = Instant.parse("2026-11-20T02:00:00Z")
            assertThat(save(bank, Money(100L), moment, zone)).isFalse()
            assertThat(save(archived, Money(100L), moment, zone)).isFalse()
            assertThat(save(fund, Money(-1L), moment, zone)).isFalse()
            assertThat(valuations.observeAll().first()).isEmpty()
        }
    }

    /** BR-054: two saves on the same local day keep only the newer value. */
    @Test
    fun givenTwoValuesOnSameDayWhenSavedThenOnlyTheNewerRemains() {
        runBlocking {
            val fund = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.MUTUAL_FUND))
            assertThat(save(fund, Money(1_000L), Instant.parse("2026-11-20T01:00:00Z"), zone)).isTrue()
            assertThat(save(fund, Money(2_000L), Instant.parse("2026-11-20T05:00:00Z"), zone)).isTrue()
            assertThat(valuations.observeAll().first().map { it.value }).isEqualTo(listOf(Money(2_000L)))
        }
    }

    /** BR-054: a value on a different local day is kept next to the earlier one. */
    @Test
    fun givenValuesOnDifferentDaysWhenSavedThenBothAreKept() {
        runBlocking {
            val fund = wallets.insert(walletOf(WalletType.INVESTMENT, InvestKind.MUTUAL_FUND))
            save(fund, Money(1_000L), Instant.parse("2026-11-20T01:00:00Z"), zone)
            save(fund, Money(2_000L), Instant.parse("2026-11-21T01:00:00Z"), zone)
            assertThat(valuations.observeAll().first()).hasSize(2)
        }
    }
}
