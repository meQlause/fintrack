package com.meqlause.fintrack.core.testing.repository

import com.meqlause.fintrack.core.testing.contract.InstallmentPlanRepositoryContractTest

/** ADR-007: runs the instalment plan contract against the in-memory fake. */
class FakeInstallmentPlanRepositoryTest : InstallmentPlanRepositoryContractTest() {
    override fun createFixture() = FakeRepositoryFixture()
}
