package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.component.ScreenGroup
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.settings.R

// FR-120, FR-121, S-12: stateless Pengaturan screen; rows and the open drawer come from SettingsUiState
@Composable
internal fun SettingsScreen(
    state: SettingsUiState,
    callbacks: SettingsCallbacks,
    modifier: Modifier = Modifier,
    backup: BackupUiState = BackupUiState(),
) {
    Column(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        ScreenBackHeader(
            title = stringResource(R.string.settings_title),
            backLabel = stringResource(R.string.settings_back),
            onBack = callbacks.onBack,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = FintrackScreenSizes.screenGutter)
                    .padding(bottom = FintrackScreenSizes.screenBottom),
        ) {
            SettingsSection(title = stringResource(R.string.settings_section_general)) {
                SettingsRow(
                    label = stringResource(R.string.settings_theme_label),
                    value = themeLabel(state.themeMode),
                    onClick = { callbacks.onOpenSheet(SettingsSheet.THEME) },
                )
                SettingsRow(
                    showDivider = true,
                    label = stringResource(R.string.settings_period_label),
                    value = stringResource(R.string.settings_period_value, state.periodStartDay),
                    onClick = { callbacks.onOpenSheet(SettingsSheet.PERIOD_DAY) },
                )
            }
            SettingsSection(title = stringResource(R.string.settings_section_data)) {
                SettingsRow(
                    label = stringResource(R.string.settings_categories_label),
                    value = "",
                    onClick = callbacks.onOpenCategories,
                )
                SettingsRow(
                    showDivider = true,
                    label = stringResource(R.string.settings_budgets_label),
                    value = "",
                    onClick = callbacks.onOpenBudgets,
                )
            }
            SettingsSection(title = stringResource(R.string.settings_section_detection)) {
                SettingsRow(
                    label = stringResource(R.string.settings_monitored_label),
                    value = "",
                    onClick = callbacks.onOpenMonitoredApps,
                )
                SettingsRow(
                    showDivider = true,
                    label = stringResource(R.string.settings_battery_label),
                    value = "",
                    onClick = callbacks.onOpenBatteryHelp,
                )
            }
            SettingsSection(title = stringResource(R.string.settings_section_security)) {
                SecurityRows(state = state, callbacks = callbacks)
            }
            SettingsSection(title = stringResource(R.string.settings_section_about)) {
                SettingsRow(
                    label = stringResource(R.string.settings_about_label),
                    value = "",
                    onClick = callbacks.onOpenAbout,
                )
            }
            SettingsSection(title = stringResource(R.string.settings_section_backup)) {
                BackupRows(backup = backup, callbacks = callbacks)
            }
        }
    }
    SettingsSheetHost(state = state, callbacks = callbacks)
}

// FR-120, FR-121: a grey section label above one card of rows
@Composable
internal fun SettingsSection(
    title: String,
    padded: Boolean = false,
    content: @Composable () -> Unit,
) {
    ScreenGroup(label = title) {
        if (padded) {
            Column(modifier = Modifier.padding(FintrackScreenSizes.rowPaddingHorizontal)) { content() }
        } else {
            content()
        }
    }
}

// FR-100: a PIN is set up when none exists; once set it can be changed or removed, and both ask for the current PIN
@Composable
internal fun SecurityRows(
    state: SettingsUiState,
    callbacks: SettingsCallbacks,
) {
    if (state.pinEnabled) {
        SettingsRow(
            label = stringResource(R.string.settings_pin_change_label),
            value = "",
            onClick = callbacks.onOpenPinChange,
        )
        SettingsRow(
            label = stringResource(R.string.settings_pin_remove_label),
            value = "",
            onClick = callbacks.onOpenPinRemove,
            showDivider = true,
        )
    } else {
        SettingsRow(
            label = stringResource(R.string.settings_pin_setup_label),
            value = "",
            onClick = callbacks.onOpenPinSetup,
        )
    }
    if (state.pinEnabled && state.biometricSupported) {
        SwitchRow(
            label = stringResource(R.string.settings_biometric_label),
            checked = state.biometricEnabled,
            showDivider = true,
            onCheckedChange = callbacks.onToggleBiometric,
        )
    }
    SwitchRow(
        label = stringResource(R.string.settings_hide_recents_label),
        checked = state.hideFromRecents,
        showDivider = true,
        onCheckedChange = callbacks.onToggleHideFromRecents,
    )
}
