package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.WalletRepositoryContractTest

/** ADR-007: runs the wallet contract against the in-memory fake. */
class FakeWalletRepositoryTest : WalletRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
