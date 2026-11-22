package com.meqlause.fintrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.category.navigation.categoryDestination
import com.meqlause.fintrack.feature.onboarding.OnboardingDestination
import com.meqlause.fintrack.feature.onboarding.onboardingGraph
import com.meqlause.fintrack.tab.PlaceholderTabScreen

// DOC-03: app owns the NavHost; onboarding (FR-001) starts when it is needed, otherwise Beranda is the start
@Composable
internal fun FintrackNavHost(
    navController: NavHostController,
    onboardingNeeded: Boolean,
    modifier: Modifier = Modifier,
) {
    if (onboardingNeeded) {
        NavHost(navController = navController, startDestination = OnboardingDestination, modifier = modifier) {
            fintrackGraph(navController)
        }
    } else {
        NavHost(navController = navController, startDestination = HomeRoute, modifier = modifier) {
            fintrackGraph(navController)
        }
    }
}

// DOC-03: the routes of the app; onboarding leads to Beranda and its PIN offer has no screen until feature:lock
private fun NavGraphBuilder.fintrackGraph(navController: NavHostController) {
    onboardingGraph(
        onFinished = {
            navController.navigate(HomeRoute) {
                popUpTo<OnboardingDestination> { inclusive = true }
            }
        },
        // FR-003: the PIN setup screen arrives with feature:lock (T8.1); until then the offer does nothing
        onSetupPin = {},
    )
    composable<HomeRoute> { PlaceholderTabScreen(mainTab = MainTab.HOME) }
    composable<HistoryRoute> { PlaceholderTabScreen(mainTab = MainTab.HISTORY) }
    composable<InvestmentRoute> { PlaceholderTabScreen(mainTab = MainTab.INVESTMENT) }
    composable<WalletRoute> { PlaceholderTabScreen(mainTab = MainTab.WALLET) }
    // DOC-09, FR-020: Kategori is reached from Settings > Data (T4.11 adds the entry point)
    categoryDestination(onBack = { navController.popBackStack() })
}
