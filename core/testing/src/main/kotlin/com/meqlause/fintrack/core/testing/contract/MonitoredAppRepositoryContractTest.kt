package com.meqlause.fintrack.core.testing.contract

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.meqlause.fintrack.core.testing.data.aMonitoredApp
import com.meqlause.fintrack.core.testing.data.aWallet
import com.meqlause.fintrack.core.testing.data.anInstant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/** DOC-03, FR-090: the contract of MonitoredAppRepository, run against the fake and the Local implementation. */
abstract class MonitoredAppRepositoryContractTest {
    private val fixture: RepositoryFixture by lazy { createFixture() }

    /** ADR-007: a fresh, empty store for one test. */
    protected abstract fun createFixture(): RepositoryFixture

    /** ADR-007: releases the store after each test. */
    @After
    fun closeFixture() {
        fixture.close()
    }

    /** DOC-02: a fresh store has no monitored apps. */
    @Test
    fun givenNoMonitoredAppsWhenObservedThenTheListIsEmpty() =
        runTest {
            assertThat(fixture.monitoredApps.observeAll().first()).isEmpty()
        }

    /** FR-090: an app with a default wallet and a last notification keeps both. */
    @Test
    fun givenAppWithDefaultWalletWhenObservedThenEveryFieldIsKept() =
        runTest {
            val walletId = fixture.wallets.insert(aWallet())
            val monitored =
                aMonitoredApp(
                    "com.example.bank",
                    walletId,
                ).copy(lastNotificationAt = anInstant(offsetMillis = 5_000L))

            fixture.monitoredApps.insert(monitored)

            assertThat(fixture.monitoredApps.observeAll().first()).isEqualTo(listOf(monitored))
        }

    /** FR-090: the package name is the key, so a second registration of it is rejected. */
    @Test
    fun givenAppRegisteredWhenTheSamePackageIsInsertedAgainThenItIsRejected() =
        runTest {
            fixture.monitoredApps.insert(aMonitoredApp("com.example.wallet"))

            val failure =
                runCatching { fixture.monitoredApps.insert(aMonitoredApp("com.example.wallet")) }
                    .exceptionOrNull()

            assertThat(failure).isNotNull()
            assertThat(fixture.monitoredApps.observeAll().first()).hasSize(1)
        }

    /** DOC-02: apps come back ordered by package name. */
    @Test
    fun givenAppsInsertedOutOfOrderWhenObservedThenPackageNameDecides() =
        runTest {
            fixture.monitoredApps.insert(aMonitoredApp("com.example.zeta"))
            fixture.monitoredApps.insert(aMonitoredApp("com.example.alpha"))

            val packages =
                fixture.monitoredApps
                    .observeAll()
                    .first()
                    .map { it.packageName }

            assertThat(packages).isEqualTo(listOf("com.example.alpha", "com.example.zeta"))
        }
}
