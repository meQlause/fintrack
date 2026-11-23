package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.ThemeMode
import com.meqlause.fintrack.core.ui.picker.DayGridDrawer

// FR-120, FR-121, S-18: shows the picker drawer that is open in SettingsUiState, if any
@Composable
internal fun SettingsSheetHost(
    state: SettingsUiState,
    callbacks: SettingsCallbacks,
) {
    when (state.sheet) {
        SettingsSheet.PERIOD_DAY -> {
            DayGridDrawer(
                title = stringResource(R.string.settings_period_sheet_title),
                selectedDay = state.periodStartDay,
                onPick = callbacks.preferences.onPickPeriodStartDay,
                onClose = callbacks.preferences.onDismissSheet,
            )
        }

        null -> {
            Unit
        }
    }
}

// FR-120: Indonesian label of a theme choice, shared by the theme chips
@Composable
internal fun themeLabel(mode: ThemeMode): String =
    when (mode) {
        ThemeMode.SYSTEM -> stringResource(R.string.settings_theme_system)
        ThemeMode.LIGHT -> stringResource(R.string.settings_theme_light)
        ThemeMode.DARK -> stringResource(R.string.settings_theme_dark)
    }
