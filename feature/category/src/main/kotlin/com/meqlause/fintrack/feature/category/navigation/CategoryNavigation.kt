package com.meqlause.fintrack.feature.category.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.category.CategoryRoute

// DOC-09, FR-020: registers the Kategori destination in the host graph
public object CategoryNavigation {
    public fun register(
        builder: NavGraphBuilder,
        onBack: () -> Unit,
    ) {
        builder.composable<CategoryDestination> { CategoryRoute(onBack = onBack) }
    }
}
