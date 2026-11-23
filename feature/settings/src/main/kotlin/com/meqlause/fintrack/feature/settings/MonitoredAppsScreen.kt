package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.BackAction
import com.meqlause.fintrack.core.designsystem.component.HeaderTitle
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.picker.OptionChoices
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
import com.meqlause.fintrack.core.ui.picker.PickerActions
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val LAST_RECEIVED_FORMAT: DateTimeFormatter =
    DateTimeFormatter.ofPattern(
        "d MMM, HH:mm",
        Locale.forLanguageTag("id-ID"),
    )

// FR-081, FR-082, FR-090, S-13: stateless Aplikasi dipantau screen; rows and drawer come from MonitoredAppsUiState
@Composable
internal fun MonitoredAppsScreen(
    state: MonitoredAppsUiState,
    callbacks: MonitoredAppsCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        ScreenBackHeader(
            title = HeaderTitle(text = stringResource(R.string.monitored_title)),
            back = BackAction(label = stringResource(R.string.settings_back), onBack = callbacks.onBack),
        )
        MonitoredAppsContent(state = state, callbacks = callbacks)
    }
    if (state.pickingPackage != null) {
        WalletDrawer(state = state, callbacks = callbacks)
    }
}

// FR-081, FR-090: the access row, the diagnostic counts and one row per supported app, in one scrolling column
@Composable
private fun MonitoredAppsContent(
    state: MonitoredAppsUiState,
    callbacks: MonitoredAppsCallbacks,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = FintrackScreenSizes.screenGutter)
                .padding(bottom = FintrackScreenSizes.screenBottom),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        SettingsSection(title = stringResource(R.string.monitored_section_access)) {
            SettingsRow(
                label = stringResource(R.string.monitored_access_label),
                value = accessLabel(state.accessGranted),
                onClick = callbacks.onOpenAccessSettings,
            )
        }
        SettingsSection(title = stringResource(R.string.monitored_section_diagnostics), padded = true) {
            Text(
                text = stringResource(R.string.monitored_counts_value, state.ignoredCount, state.unrecognizedCount),
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
        SettingsSection(title = stringResource(R.string.monitored_section_apps), padded = true) {
            state.apps.forEach { row -> AppRow(monitoredApp = row, callbacks = callbacks) }
        }
    }
}

// FR-082: the wallet drawer of the app whose default wallet is being chosen, the current one marked
@Composable
private fun WalletDrawer(
    state: MonitoredAppsUiState,
    callbacks: MonitoredAppsCallbacks,
) {
    OptionsDrawer(
        title = stringResource(R.string.monitored_wallet_sheet_title),
        choices =
            OptionChoices(
                options = state.wallets,
                selectedKey =
                    state.apps
                        .firstOrNull { it.packageName == state.pickingPackage }
                        ?.defaultWalletId
                        ?.value
                        ?.toString(),
            ),
        actions = PickerActions(onPick = callbacks.onPickWallet, onClose = callbacks.onDismissPicker),
    )
}

// FR-081, FR-082, FR-090: one supported app with its switch, default wallet and last received time
@Composable
private fun AppRow(
    monitoredApp: MonitoredAppRow,
    callbacks: MonitoredAppsCallbacks,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = monitoredApp.displayName,
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.texts.onSurface,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = monitoredApp.isEnabled,
                onCheckedChange = { callbacks.onToggle(monitoredApp.packageName, it) },
            )
        }
        SettingsRow(
            label = stringResource(R.string.monitored_wallet_label),
            value = monitoredApp.defaultWalletName ?: stringResource(R.string.monitored_wallet_none),
            onClick = { callbacks.onOpenPicker(monitoredApp.packageName) },
        )
        Text(
            text = lastReceivedLabel(monitoredApp.lastNotificationAt),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

// FR-090: the access state as Indonesian copy; the row opens the system settings
@Composable
private fun accessLabel(granted: Boolean): String =
    stringResource(if (granted) R.string.monitored_access_on else R.string.monitored_access_off)

// FR-090: the last time a notification of this app was received, or the none copy
@Composable
private fun lastReceivedLabel(receivedAt: Instant?): String =
    if (receivedAt == null) {
        stringResource(R.string.monitored_last_none)
    } else {
        stringResource(
            R.string.monitored_last_format,
            LAST_RECEIVED_FORMAT.format(receivedAt.atZone(ZoneId.systemDefault())),
        )
    }
