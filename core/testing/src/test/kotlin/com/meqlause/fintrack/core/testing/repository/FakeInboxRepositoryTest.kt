package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.InboxRepositoryContractTest

/** ADR-007: runs the inbox contract against the in-memory fake. */
class FakeInboxRepositoryTest : InboxRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
