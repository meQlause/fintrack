package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.support.LocalRepositoryFixture
import com.meqlause.fintrack.core.data.support.openInMemoryDatabase
import com.meqlause.fintrack.core.testing.contract.RepositoryFixture
import com.meqlause.fintrack.core.testing.contract.WalletRepositoryContractTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02, FR-010: runs the wallet contract against the Local implementation and in-memory Room. */
@RunWith(RobolectricTestRunner::class)
class LocalWalletRepositoryTest : WalletRepositoryContractTest() {
    override fun createFixture(): RepositoryFixture = LocalRepositoryFixture(openInMemoryDatabase())
}
