package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.LoanRepositoryContractTest

/** ADR-007: runs the loan contract against the in-memory fake. */
class FakeLoanRepositoryTest : LoanRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
