package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.support.LocalRepositoryFixture
import com.meqlause.fintrack.core.data.support.openInMemoryDatabase
import com.meqlause.fintrack.core.testing.contract.InboxRepositoryContractTest
import com.meqlause.fintrack.core.testing.contract.RepositoryFixture
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02, FR-090: runs the inbox contract against the Local implementation and in-memory Room. */
@RunWith(RobolectricTestRunner::class)
class LocalInboxRepositoryTest : InboxRepositoryContractTest() {
    override fun createFixture(): RepositoryFixture = LocalRepositoryFixture(openInMemoryDatabase())
}
