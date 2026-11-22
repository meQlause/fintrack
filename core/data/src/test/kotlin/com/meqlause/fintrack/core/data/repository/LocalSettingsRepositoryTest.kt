package com.meqlause.fintrack.core.data.repository

import com.meqlause.fintrack.core.data.support.LocalRepositoryFixture
import com.meqlause.fintrack.core.data.support.openInMemoryDatabase
import com.meqlause.fintrack.core.testing.contract.RepositoryFixture
import com.meqlause.fintrack.core.testing.contract.SettingsRepositoryContractTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** DOC-02: runs the settings contract against the DataStore-backed implementation. */
@RunWith(RobolectricTestRunner::class)
class LocalSettingsRepositoryTest : SettingsRepositoryContractTest() {
    override fun createFixture(): RepositoryFixture = LocalRepositoryFixture(openInMemoryDatabase())
}
