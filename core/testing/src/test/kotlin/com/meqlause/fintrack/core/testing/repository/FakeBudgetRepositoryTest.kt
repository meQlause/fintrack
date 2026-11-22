package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.BudgetRepositoryContractTest

/** ADR-007: runs the budget contract against the in-memory fake. */
class FakeBudgetRepositoryTest : BudgetRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
