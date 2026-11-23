package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.ThemeMode
import com.meqlause.fintrack.core.ui.picker.DayGridDrawer
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
import com.meqlause.fintrack.core.ui.picker.PickerOption
import com.meqlause.fintrack.feature.settings.R

// FR-120, FR-121, S-18: shows the picker drawer that is open in SettingsUiState, if any
@Composable
internal fun SettingsSheetHost(
    state: SettingsUiState,
    callbacks: SettingsCallbacks,
) {
    when (state.sheet) {
        SettingsSheet.THEME -> {
            OptionsDrawer(
                title = stringResource(R.string.settings_theme_sheet_title),
                options = themeOptions(),
                selectedKey = state.themeMode.name,
                onPick = { option -> callbacks.onPickThemeMode(ThemeMode.valueOf(option.optionKey)) },
                onClose = callbacks.onDismissSheet,
            )
        }

        SettingsSheet.PERIOD_DAY -> {
            DayGridDrawer(
                title = stringResource(R.string.settings_period_sheet_title),
                selectedDay = state.periodStartDay,
                onPick = callbacks.onPickPeriodStartDay,
                onClose = callbacks.onDismissSheet,
            )
        }

        null -> {
            Unit
        }
    }
}

// FR-120: the three theme choices in their stored order
@Composable
private fun themeOptions(): List<PickerOption> =
    ThemeMode.entries.map { mode -> PickerOption(optionKey = mode.name, label = themeLabel(mode)) }

// FR-120: Indonesian label of a theme choice, shared by the row value and the drawer
@Composable
internal fun themeLabel(mode: ThemeMode): String =
    when (mode) {
        ThemeMode.SYSTEM -> stringResource(R.string.settings_theme_system)
        ThemeMode.LIGHT -> stringResource(R.string.settings_theme_light)
        ThemeMode.DARK -> stringResource(R.string.settings_theme_dark)
    }
