package com.meqlause.fintrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.meqlause.fintrack.tab.PlaceholderTabScreen

// DOC-03: app owns the NavHost; each tab is a type-safe route and Beranda is the start destination
@Composable
internal fun FintrackNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = HomeRoute, modifier = modifier) {
        composable<HomeRoute> { PlaceholderTabScreen(mainTab = MainTab.HOME) }
        composable<HistoryRoute> { PlaceholderTabScreen(mainTab = MainTab.HISTORY) }
        composable<InvestmentRoute> { PlaceholderTabScreen(mainTab = MainTab.INVESTMENT) }
        composable<WalletRoute> { PlaceholderTabScreen(mainTab = MainTab.WALLET) }
    }
}
