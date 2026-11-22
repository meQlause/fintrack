package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.support.LocalRepositoryFixture
import com.meqlause.fintrack.core.data.support.openInMemoryDatabase
import com.meqlause.fintrack.core.testing.contract.InstallmentPlanRepositoryContractTest
import com.meqlause.fintrack.core.testing.contract.RepositoryFixture
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02, FR-140: runs the instalment plan contract against the Local implementation and in-memory Room. */
@RunWith(RobolectricTestRunner::class)
class LocalInstallmentPlanRepositoryTest : InstallmentPlanRepositoryContractTest() {
    override fun createFixture(): RepositoryFixture = LocalRepositoryFixture(openInMemoryDatabase())
}
