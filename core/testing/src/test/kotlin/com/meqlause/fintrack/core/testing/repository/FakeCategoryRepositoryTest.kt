package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.CategoryRepositoryContractTest

/** ADR-007: runs the category contract against the in-memory fake. */
class FakeCategoryRepositoryTest : CategoryRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
