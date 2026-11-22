package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.testing.data.aValuation
import com.meqlause.fintrack.core.testing.data.aWallet
import com.meqlause.fintrack.core.testing.data.anInstant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-172: the contract of ValuationRepository, run against the fake and the Local implementation. */
abstract class ValuationRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a fresh store has no valuations. */
    @Test
    fun givenNoValuationsWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.valuations.observeAll().first()).isEmpty()
        }

    /** FR-172: a valuation comes back unchanged except for its generated id. */
    @Test
    fun givenValuationOfAnInvestmentWalletWhenObservedThenEveryFieldIsKept() =
        runTest {
            val walletId = fixture.wallets.insert(aWallet(name = "Reksa dana"))
            val valuation = aValuation(walletId)

            val valuationId = fixture.valuations.insert(valuation)

            assertThat(
                fixture.valuations.observeAll().first(),
            ).isEqualTo(listOf(valuation.copy(valuationId = valuationId)))
        }

    /** DOC-02: valuations come back in the order they were valued. */
    @Test
    fun givenValuationsInsertedOutOfOrderWhenObservedThenValuedAtDecides() =
        runTest {
            val walletId = fixture.wallets.insert(aWallet(name = "Emas"))
            fixture.valuations.insert(aValuation(walletId, valuedAt = anInstant(offsetMillis = 2_000L)))
            fixture.valuations.insert(aValuation(walletId, valuedAt = anInstant(offsetMillis = 1_000L)))

            val valuedAt =
                fixture.valuations
                    .observeAll()
                    .first()
                    .map { it.valuedAt }

            assertThat(valuedAt).isEqualTo(listOf(anInstant(offsetMillis = 1_000L), anInstant(offsetMillis = 2_000L)))
        }
}
