package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.MerchantRuleRepositoryContractTest

/** ADR-007: runs the merchant rule contract against the in-memory fake. */
class FakeMerchantRuleRepositoryTest : MerchantRuleRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
