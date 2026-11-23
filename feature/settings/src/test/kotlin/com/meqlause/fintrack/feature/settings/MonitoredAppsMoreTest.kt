package com.meqlause.fintrack.feature.settings

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.detection.DetectionCounters
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.repository.FakeMonitoredAppRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
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
import java.time.Instant

// FR-081, FR-082, S-13: the wallet drawer, the default wallet names and the switch of MonitoredAppsViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MonitoredAppsMoreTest {
    private val dispatcher = StandardTestDispatcher()
    private val apps = FakeMonitoredAppRepository()
    private val wallets = FakeWalletRepository(FakeTransactionRepository())
    private val counters = DetectionCounters()

    @Before
    fun setMainDispatcher() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun resetMainDispatcher() {
        Dispatchers.resetMain()
    }

    private fun viewModel(): MonitoredAppsViewModel =
        MonitoredAppsViewModel(apps, wallets, FakeNotificationAccess(granted = true), counters)

    private suspend fun wallet(
        name: String,
        archived: Boolean = false,
    ): WalletId =
        wallets.insert(
            Wallet(
                walletId = WalletId(0L),
                kind = WalletKind(WalletType.BANK, investKind = null),
                terms = WalletTerms(Money(0L), credit = null),
                profile = WalletProfile(name, Appearance("bank", "blue"), sortOrder = 0),
                status = WalletStatus(isArchived = archived),
                audit = Audit(Instant.EPOCH, Instant.EPOCH),
            ),
        )

    /** FR-082: the drawer lists live wallets, a pick names the default wallet, and the drawer closes. */
    @Test
    fun `given wallets when one is picked then the app appRow names it and archived ones are not offered (FR-082)`() =
        runTest(dispatcher) {
            val bank = wallet("BCA")
            wallet("Lama", archived = true)
            val model = viewModel()
            model.openPicker(BCA_APP)
            advanceUntilIdle()
            val open = model.state.first { it.pickingPackage == BCA_APP }
            assertThat(open.wallets.map { it.label }).isEqualTo(listOf("BCA"))
            model.pickWallet(PickerOption(bank.value.toString(), "BCA"))
            advanceUntilIdle()
            val appRow =
                model.state.first {
                    it.pickingPackage == null &&
                        it.apps.any { app -> app.defaultWalletName != null }
                }
            assertThat(appRow.apps.first { it.packageName == BCA_APP }.defaultWalletName).isEqualTo("BCA")
        }

    /** FR-082: dismissing the drawer closes it, and a pick without a drawer stores nothing. */
    @Test
    fun `given no drawer when a wallet is picked then nothing is stored and dismiss closes the drawer (FR-082)`() =
        runTest(dispatcher) {
            val model = viewModel()
            model.pickWallet(PickerOption("3", "x"))
            advanceUntilIdle()
            assertThat(apps.find(BCA_APP)).isNull()
            model.openPicker(BCA_APP)
            model.dismissPicker()
            advanceUntilIdle()
            assertThat(model.state.first { it.apps.isNotEmpty() }.pickingPackage).isNull()
        }

    /** FR-081: an app switched on can be switched off again, keeping its record. */
    @Test
    fun `given an enabled app when it is switched off then it is stored disabled (FR-081)`() =
        runTest(dispatcher) {
            val model = viewModel()
            model.setEnabled(BCA_APP, true)
            advanceUntilIdle()
            model.setEnabled(BCA_APP, false)
            advanceUntilIdle()
            assertThat(apps.find(BCA_APP)?.isEnabled).isEqualTo(false)
        }

    /** FR-085: both diagnostic counts reach the state. */
    @Test
    fun `given recorded notices when the screen opens then both counts are shown (FR-085)`() =
        runTest(dispatcher) {
            counters.countUnrecognized()
            counters.countIgnored()
            val state = viewModel().state.first { it.apps.isNotEmpty() }
            assertThat(state.unrecognizedCount).isEqualTo(1)
            assertThat(state.ignoredCount).isEqualTo(1)
        }

    private companion object {
        const val BCA_APP = "com.bca"
    }
}
