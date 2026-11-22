package com.meqlause.fintrack.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.meqlause.fintrack.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// DOC-09: the four tabs of the bottom bar, in bar order; the centre add button is not a tab
enum class MainTab(
    val labelRes: Int,
    val titleRes: Int,
    val iconRes: Int,
) {
    HOME(R.string.tab_home, R.string.app_name, DesignR.drawable.ic_home),
    HISTORY(R.string.tab_history, R.string.tab_history, DesignR.drawable.ic_history),
    INVESTMENT(R.string.tab_investment, R.string.tab_investment, DesignR.drawable.ic_trend),
    WALLET(R.string.tab_wallet, R.string.tab_wallet, DesignR.drawable.ic_wallet),
    ;

    // DOC-09: a tab is selected while its own route is on the current destination
    fun isSelectedIn(destination: NavDestination?): Boolean = destination?.route == routeName()

    // DOC-09: a type-safe route is identified by its class name, the default serial name NavDestination.route carries
    private fun routeName(): String =
        when (this) {
            HOME -> HomeRoute::class.java.name
            HISTORY -> HistoryRoute::class.java.name
            INVESTMENT -> InvestmentRoute::class.java.name
            WALLET -> WalletRoute::class.java.name
        }

    // DOC-09: switching tabs keeps each tab's back stack and never stacks the same tab twice
    fun navigateFrom(controller: NavHostController) {
        val options: NavOptionsBuilder.() -> Unit = {
            popUpTo(controller.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
        when (this) {
            HOME -> controller.navigate(HomeRoute, options)
            HISTORY -> controller.navigate(HistoryRoute, options)
            INVESTMENT -> controller.navigate(InvestmentRoute, options)
            WALLET -> controller.navigate(WalletRoute, options)
        }
    }
}
