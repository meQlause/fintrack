package com.meqlause.fintrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meqlause.fintrack.feature.category.navigation.CategoryDestination
import com.meqlause.fintrack.feature.category.navigation.categoryDestination
import com.meqlause.fintrack.feature.onboarding.OnboardingDestination
import com.meqlause.fintrack.feature.onboarding.onboardingGraph
import com.meqlause.fintrack.feature.settings.navigation.SettingsDestination
import com.meqlause.fintrack.feature.settings.navigation.settingsDestination
import com.meqlause.fintrack.feature.transaction.edit.navigation.EditTransactionDestination
import com.meqlause.fintrack.feature.transaction.edit.navigation.editTransactionDestination
import com.meqlause.fintrack.feature.transaction.history.navigation.historyDestination
import com.meqlause.fintrack.feature.wallet.navigation.WalletDetailDestination
import com.meqlause.fintrack.feature.wallet.navigation.walletDestination
import com.meqlause.fintrack.feature.wallet.navigation.walletDetailDestination
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
    composable<HomeRoute> {
        PlaceholderTabScreen(
            mainTab = MainTab.HOME,
            onSettingsClick = { navController.navigate(SettingsDestination) },
        )
    }
    // DOC-09, FR-036: Riwayat (S-05); a tapped row opens the S-04 edit sheet for that transaction
    historyDestination(onOpenTransaction = { id ->
        navController.navigate(EditTransactionDestination(transactionId = id))
    })
    composable<InvestmentRoute> { PlaceholderTabScreen(mainTab = MainTab.INVESTMENT) }
    walletDestination(onOpenDetail = { navController.navigate(WalletDetailDestination(it.value)) })
    // FR-017, S-19: Pakai and Transfer open the add sheet (S-04); preselecting the wallet/type is a later step
    walletDetailDestination(
        onBack = { navController.popBackStack() },
        onUse = { navController.navigate(EditTransactionDestination()) },
        onTransfer = { navController.navigate(EditTransactionDestination()) },
    )
    // DOC-09, S-12: Pengaturan is opened from the Beranda header; its Kategori row leads to S-10 (FR-020)
    settingsDestination(
        onBack = { navController.popBackStack() },
        onOpenCategories = { navController.navigate(CategoryDestination) },
    )
    categoryDestination(onBack = { navController.popBackStack() })
    // DOC-09, S-04: the add or edit sheet returns to the screen it was opened from
    editTransactionDestination(onDismiss = { navController.popBackStack() })
}
