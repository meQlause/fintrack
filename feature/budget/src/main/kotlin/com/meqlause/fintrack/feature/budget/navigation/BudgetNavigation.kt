package com.meqlause.fintrack.feature.budget.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.budget.BudgetRoute

// DOC-09, FR-070: registers the Anggaran destination in the host graph
public fun NavGraphBuilder.budgetDestination(onBack: () -> Unit) {
    composable<BudgetDestination> { BudgetRoute(onBack = onBack) }
}
