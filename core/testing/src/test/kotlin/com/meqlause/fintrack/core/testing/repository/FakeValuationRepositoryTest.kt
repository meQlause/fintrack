package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.ValuationRepositoryContractTest

/** ADR-007: runs the valuation contract against the in-memory fake. */
class FakeValuationRepositoryTest : ValuationRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
