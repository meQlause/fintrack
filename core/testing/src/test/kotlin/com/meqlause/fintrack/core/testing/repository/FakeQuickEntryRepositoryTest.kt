package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.QuickEntryRepositoryContractTest

/** ADR-007: runs the quick entry contract against the in-memory fake. */
class FakeQuickEntryRepositoryTest : QuickEntryRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
