package com.meqlause.fintrack.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.settings.SettingsRoute

// DOC-09, FR-120, FR-121: registers Pengaturan in the host graph; the app wires the Kategori navigation
public fun NavGraphBuilder.settingsDestination(
    onBack: () -> Unit,
    onOpenCategories: () -> Unit,
) {
    composable<SettingsDestination> {
        SettingsRoute(onBack = onBack, onOpenCategories = onOpenCategories)
    }
}
