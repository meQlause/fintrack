package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.meqlause.fintrack.core.testing.data.aCategory
import com.meqlause.fintrack.core.testing.data.aMerchantRule
import com.meqlause.fintrack.core.testing.data.aWallet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-088: the contract of MerchantRuleRepository, run against the fake and the Local implementation. */
abstract class MerchantRuleRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a fresh store has no merchant rules. */
    @Test
    fun givenNoMerchantRulesWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.merchantRules.observeAll().first()).isEmpty()
        }

    /** FR-088: a rule with a wallet comes back unchanged except for its generated id. */
    @Test
    fun givenRuleWithAWalletWhenObservedThenEveryFieldIsKept() =
        runTest {
            val walletId = fixture.wallets.insert(aWallet())
            val categoryId = fixture.categories.insert(aCategory())
            val rule = aMerchantRule("indomaret", categoryId, walletId)

            val merchantRuleId = fixture.merchantRules.insert(rule)

            assertThat(
                fixture.merchantRules.observeAll().first(),
            ).isEqualTo(listOf(rule.copy(merchantRuleId = merchantRuleId)))
        }

    /** FR-088: a rule without a wallet keeps the wallet empty. */
    @Test
    fun givenRuleWithoutAWalletWhenObservedThenTheWalletStaysEmpty() =
        runTest {
            val categoryId = fixture.categories.insert(aCategory())
            val rule = aMerchantRule("alfamart", categoryId, walletId = null)

            val merchantRuleId = fixture.merchantRules.insert(rule)

            assertThat(
                fixture.merchantRules.observeAll().first(),
            ).isEqualTo(listOf(rule.copy(merchantRuleId = merchantRuleId)))
        }

    /** FR-088: one rule per normalized merchant key; a second rule for the same key is rejected. */
    @Test
    fun givenRuleForAMerchantKeyWhenASecondOneIsInsertedThenItIsRejected() =
        runTest {
            val categoryId = fixture.categories.insert(aCategory())
            fixture.merchantRules.insert(aMerchantRule("kopi kenangan", categoryId))

            val failure =
                runCatching { fixture.merchantRules.insert(aMerchantRule("kopi kenangan", categoryId)) }
                    .exceptionOrNull()

            assertThat(failure).isNotNull()
            assertThat(fixture.merchantRules.observeAll().first()).hasSize(1)
        }

    /** DOC-02: rules come back ordered by their normalized key. */
    @Test
    fun givenRulesInsertedOutOfOrderWhenObservedThenMerchantKeyDecides() =
        runTest {
            val categoryId = fixture.categories.insert(aCategory())
            fixture.merchantRules.insert(aMerchantRule("warung", categoryId))
            fixture.merchantRules.insert(aMerchantRule("alfamart", categoryId))

            val keys =
                fixture.merchantRules
                    .observeAll()
                    .first()
                    .map { it.merchantKey }

            assertThat(keys).isEqualTo(listOf("alfamart", "warung"))
        }
}
