package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.component.ScreenGroup
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-120, FR-121, S-12: stateless Pengaturan screen; one page, sections in the order of the prototype
@Composable
internal fun SettingsScreen(
    state: SettingsUiState,
    callbacks: SettingsCallbacks,
    backup: BackupUiState = BackupUiState(),
    version: String = "",
) {
    Column(modifier = Modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        ScreenBackHeader(
            title = stringResource(R.string.settings_title),
            backLabel = stringResource(R.string.settings_back),
            onBack = callbacks.links.onBack,
        )
        SettingsSections(state = state, callbacks = callbacks, backup = backup, version = version)
    }
    SettingsSheetHost(state = state, callbacks = callbacks)
}

// FR-120, FR-121: the scrolling column of sections, the last one followed by the version footer
@Composable
private fun SettingsSections(
    state: SettingsUiState,
    callbacks: SettingsCallbacks,
    backup: BackupUiState,
    version: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = FintrackScreenSizes.screenGutter)
                .padding(bottom = FintrackScreenSizes.screenBottom),
    ) {
        SettingsSection(title = stringResource(R.string.settings_section_appearance)) {
            AppearanceRows(state = state, callbacks = callbacks)
        }
        SettingsSection(title = stringResource(R.string.settings_section_data)) {
            DataRows(callbacks = callbacks)
        }
        SettingsSection(title = stringResource(R.string.settings_section_detection)) {
            DetectionRows(callbacks = callbacks)
        }
        SettingsSection(title = stringResource(R.string.settings_section_security)) {
            SecurityRows(state = state, callbacks = callbacks)
        }
        SettingsSection(title = stringResource(R.string.settings_section_backup)) {
            BackupRows(backup = backup, callbacks = callbacks)
        }
        SettingsFooter(version = version, onOpenAbout = callbacks.links.onOpenAbout)
    }
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
