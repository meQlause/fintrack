package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.TransactionRepositoryContractTest

/** ADR-007: runs the transaction contract against the in-memory fake. */
class FakeTransactionRepositoryTest : TransactionRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
