package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-120, FR-121: stateful entry of S-12; the only composable that touches SettingsViewModel
@Composable
internal fun SettingsRoute(
    onBack: () -> Unit,
    onOpenCategories: () -> Unit,
    onOpenMonitoredApps: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        callbacks =
            SettingsCallbacks(
                onBack = onBack,
                onOpenSheet = viewModel::openSheet,
                onOpenCategories = onOpenCategories,
                onOpenMonitoredApps = onOpenMonitoredApps,
                onPickThemeMode = viewModel::pickThemeMode,
                onPickPeriodStartDay = viewModel::pickPeriodStartDay,
                onDismissSheet = viewModel::dismissSheet,
            ),
    )
}
