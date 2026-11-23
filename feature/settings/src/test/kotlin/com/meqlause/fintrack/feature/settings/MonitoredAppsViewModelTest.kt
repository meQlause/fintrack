package com.meqlause.fintrack.feature.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.detection.DetectionCounters
import com.meqlause.fintrack.core.domain.detection.SupportedApps
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.repository.FakeMonitoredAppRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.ui.picker.PickerOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

// FR-081, FR-082, FR-090: MonitoredAppsViewModel against the fake repositories and access check
@OptIn(ExperimentalCoroutinesApi::class)
class MonitoredAppsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val apps = FakeMonitoredAppRepository()
    private val access = FakeNotificationAccess(granted = false)
    private val counters = DetectionCounters()

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    // FR-081: a supported app that is not stored yet is listed and switched off
    @Test
    fun givenNothingStoredWhenScreenOpensThenEverySupportedAppIsOff() =
        runTest(dispatcher) {
            val state = viewModel().state.first { it.apps.isNotEmpty() }

            assertThat(state.apps.map { it.packageName }).isEqualTo(SupportedApps.displayNames.keys.toList())
            assertThat(state.apps.any { it.isEnabled }).isEqualTo(false)
        }

    // FR-081: switching an app on stores it as enabled
    @Test
    fun givenSwitchTurnedOnWhenAppChangesThenItIsStoredEnabled() =
        runTest(dispatcher) {
            viewModel().setEnabled(GOPAY, true)
            advanceUntilIdle()

            assertThat(apps.find(GOPAY)?.isEnabled).isEqualTo(true)
        }

    // FR-082: the wallet drawer stores the chosen wallet as the default of the app it was opened for
    @Test
    fun givenWalletPickedWhenDrawerOpenThenItBecomesTheDefaultWallet() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.openPicker(BCA)
            viewModel.pickWallet(PickerOption(key = "7", label = "BCA"))
            advanceUntilIdle()

            assertThat(apps.find(BCA)?.defaultWalletId).isEqualTo(WalletId(7L))
        }

    // FR-090: the access state is read again when the screen resumes after system settings changed it
    @Test
    fun givenAccessGrantedInSettingsWhenScreenResumesThenStateShowsAccess() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            access.granted = true
            viewModel.refreshAccess()

            val state = viewModel.state.first { it.accessGranted }

            assertThat(state.accessGranted).isEqualTo(true)
        }

    // FR-085, FR-090: the diagnostics show the counts the listener recorded
    @Test
    fun givenIgnoredNotificationsWhenScreenOpensThenTheCountIsShown() =
        runTest(dispatcher) {
            counters.countIgnored()
            counters.countIgnored()

            val state = viewModel().state.first { it.apps.isNotEmpty() }

            assertThat(state.ignoredCount).isEqualTo(2)
        }

    private fun viewModel(): MonitoredAppsViewModel =
        MonitoredAppsViewModel(
            monitoredApps = apps,
            wallets = FakeWalletRepository(),
            access = access,
            counters = counters,
        )

    private companion object {
        const val BCA = "com.bca"
        const val GOPAY = "com.gojek.gopay"
    }
}
