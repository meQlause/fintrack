package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.MonitoredAppRepositoryContractTest

/** ADR-007: runs the monitored app contract against the in-memory fake. */
class FakeMonitoredAppRepositoryTest : MonitoredAppRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
