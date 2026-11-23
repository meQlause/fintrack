package com.meqlause.fintrack.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.settings.AboutRoute

// DOC-09, FR-123: registers Tentang in the host graph
public fun NavGraphBuilder.aboutDestination(onBack: () -> Unit) {
    composable<AboutDestination> {
        AboutRoute(onBack = onBack)
    }
}
