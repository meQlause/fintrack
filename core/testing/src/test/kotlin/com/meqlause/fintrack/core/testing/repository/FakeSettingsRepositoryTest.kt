package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.SettingsRepositoryContractTest

/** ADR-007: runs the settings contract against the in-memory fake. */
class FakeSettingsRepositoryTest : SettingsRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
