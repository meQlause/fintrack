package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.support.LocalRepositoryFixture
import com.meqlause.fintrack.core.data.support.openInMemoryDatabase
import com.meqlause.fintrack.core.testing.contract.CategoryRepositoryContractTest
import com.meqlause.fintrack.core.testing.contract.RepositoryFixture
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02, FR-004: runs the category contract against the Local implementation and in-memory Room. */
@RunWith(RobolectricTestRunner::class)
class LocalCategoryRepositoryTest : CategoryRepositoryContractTest() {
    override fun createFixture(): RepositoryFixture = LocalRepositoryFixture(openInMemoryDatabase())
}
