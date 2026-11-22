package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.support.LocalRepositoryFixture
import com.meqlause.fintrack.core.data.support.openInMemoryDatabase
import com.meqlause.fintrack.core.testing.contract.RepositoryFixture
import com.meqlause.fintrack.core.testing.contract.ValuationRepositoryContractTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02, FR-172: runs the valuation contract against the Local implementation and in-memory Room. */
@RunWith(RobolectricTestRunner::class)
class LocalValuationRepositoryTest : ValuationRepositoryContractTest() {
    override fun createFixture(): RepositoryFixture = LocalRepositoryFixture(openInMemoryDatabase())
}
