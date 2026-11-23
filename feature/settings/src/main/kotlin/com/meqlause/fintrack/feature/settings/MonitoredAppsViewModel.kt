package com.meqlause.fintrack.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.detection.DetectionCounters
import com.meqlause.fintrack.core.domain.detection.NotificationAccess
import com.meqlause.fintrack.core.domain.detection.SupportedApps
import com.meqlause.fintrack.core.domain.repository.MonitoredAppRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.model.MonitoredApp
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.picker.PickerOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MS = 5_000L

// FR-081, FR-082, FR-090: drives S-13; every change is written through the monitored app repository
@HiltViewModel
internal class MonitoredAppsViewModel
    @Inject
    constructor(
        private val monitoredApps: MonitoredAppRepository,
        private val wallets: WalletRepository,
        private val access: NotificationAccess,
        private val counters: DetectionCounters,
    ) : ViewModel() {
        private val granted = MutableStateFlow(access.isGranted())
        private val picking = MutableStateFlow<String?>(null)

        // S-13: stored apps merged onto the supported list, with counters and the open wallet drawer
        val state: StateFlow<MonitoredAppsUiState> =
            combine(monitoredApps.observeAll(), wallets.observeAll(), granted, counters.counts, picking) {
                stored,
                walletList,
                isGranted,
                counts,
                open,
                ->
                MonitoredAppsUiState(
                    accessGranted = isGranted,
                    ignoredCount = counts.ignored,
                    unrecognizedCount = counts.unrecognized,
                    apps = rowsFor(stored, walletList),
                    wallets = walletList.filterNot { it.status.isArchived }.map { walletOption(it) },
                    pickingPackage = open,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), MonitoredAppsUiState())

        // FR-090: re-read when the screen comes back to the foreground, after the user changed system settings
        fun refreshAccess() {
            granted.value = access.isGranted()
        }

        // FR-081: enabling an app stores it when it was not stored yet
        fun setEnabled(
            packageName: String,
            enabled: Boolean,
        ) {
            updateApp(packageName) { it.copy(isEnabled = enabled) }
        }

        // FR-082: the wallet drawer for one app is open
        fun openPicker(packageName: String) {
            picking.value = packageName
        }

        fun dismissPicker() {
            picking.value = null
        }

        // FR-082: the chosen wallet becomes the app's default and the drawer closes
        fun pickWallet(option: PickerOption) {
            val packageName = picking.value ?: return
            picking.value = null
            updateApp(packageName) { it.copy(defaultWalletId = WalletId(option.optionKey.toLong())) }
        }

        private fun rowsFor(
            stored: List<MonitoredApp>,
            walletList: List<Wallet>,
        ): List<MonitoredAppRow> {
            val byPackage = stored.associateBy { it.packageName }
            return SupportedApps.displayNames.map { (packageName, name) ->
                val app = byPackage[packageName]
                MonitoredAppRow(
                    packageName = packageName,
                    displayName = name,
                    isEnabled = app?.isEnabled == true,
                    defaultWalletId = app?.defaultWalletId,
                    defaultWalletName = walletList.firstOrNull { it.walletId == app?.defaultWalletId }?.profile?.name,
                    lastNotificationAt = app?.lastNotificationAt,
                )
            }
        }

        private fun walletOption(wallet: Wallet): PickerOption =
            PickerOption(optionKey = wallet.walletId.value.toString(), label = wallet.profile.name)

        private fun updateApp(
            packageName: String,
            transform: (MonitoredApp) -> MonitoredApp,
        ) {
            viewModelScope.launch {
                val stored = monitoredApps.find(packageName) ?: newApp(packageName)
                monitoredApps.upsert(transform(stored))
            }
        }

        private fun newApp(packageName: String): MonitoredApp =
            MonitoredApp(
                packageName = packageName,
                displayName = SupportedApps.displayNames.getValue(packageName),
                isEnabled = false,
                defaultWalletId = null,
                lastNotificationAt = null,
            )
    }
