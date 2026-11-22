package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.support.LocalRepositoryFixture
import com.meqlause.fintrack.core.data.support.openInMemoryDatabase
import com.meqlause.fintrack.core.testing.contract.RepositoryFixture
import com.meqlause.fintrack.core.testing.contract.TransactionRepositoryContractTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02, FR-030: runs the transaction contract against the Local implementation and in-memory Room. */
@RunWith(RobolectricTestRunner::class)
class LocalTransactionRepositoryTest : TransactionRepositoryContractTest() {
    override fun createFixture(): RepositoryFixture = LocalRepositoryFixture(openInMemoryDatabase())
}
