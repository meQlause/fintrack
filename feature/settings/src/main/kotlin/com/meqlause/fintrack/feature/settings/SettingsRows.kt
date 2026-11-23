package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenPill
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.ThemeMode

// FR-120, S-12 "Tampilan": the theme as chips, then the first day of the period
@Composable
internal fun AppearanceRows(
    state: SettingsUiState,
    callbacks: SettingsCallbacks,
) {
    ScreenRow(
        contentPadding =
            PaddingValues(
                horizontal = FintrackScreenSizes.rowPaddingHorizontal,
                vertical = FintrackScreenSizes.rowPaddingVertical,
            ),
        leading = {},
        trailing = {
            Row(horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap)) {
                ThemeMode.entries.forEach { mode ->
                    ScreenPill(
                        label = themeLabel(mode),
                        onClick = { callbacks.onPickThemeMode(mode) },
                        selected = state.themeMode == mode,
                        height = FintrackScreenSizes.chipTall,
                    )
                }
            }
        },
    ) {
        Text(
            text = stringResource(R.string.settings_theme_label),
            style = FintrackScreenText.style(size = 14f, weight = 500),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
    SettingsRow(
        label = stringResource(R.string.settings_period_label),
        value = stringResource(R.string.settings_period_value, state.periodStartDay),
        onClick = { callbacks.onOpenSheet(SettingsSheet.PERIOD_DAY) },
        showDivider = true,
    )
}

// FR-020, FR-070: the screens that manage the categories and the budgets
@Composable
internal fun DataRows(callbacks: SettingsCallbacks) {
    SettingsRow(
        label = stringResource(R.string.settings_categories_label),
        value = "",
        onClick = callbacks.onOpenCategories,
    )
    SettingsRow(
        label = stringResource(R.string.settings_budgets_label),
        value = "",
        onClick = callbacks.onOpenBudgets,
        showDivider = true,
    )
}

// FR-081, DOC-07: the monitored apps with their diagnostics and the battery help each open their own screen
@Composable
internal fun DetectionRows(callbacks: SettingsCallbacks) {
    SettingsRow(
        label = stringResource(R.string.settings_monitored_label),
        value = "",
        onClick = callbacks.onOpenMonitoredApps,
    )
    SettingsRow(
        label = stringResource(R.string.settings_battery_label),
        value = "",
        onClick = callbacks.onOpenBatteryHelp,
        showDivider = true,
    )
}
