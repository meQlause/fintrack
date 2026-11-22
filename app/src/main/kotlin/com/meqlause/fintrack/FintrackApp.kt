package com.meqlause.fintrack

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meqlause.fintrack.core.designsystem.component.FintrackBarItem
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomBar
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomBarCallbacks
import com.meqlause.fintrack.navigation.FintrackNavHost
import com.meqlause.fintrack.navigation.MainTab

// DOC-09: the app shell: the NavHost fills the screen and the floating bottom bar sits over it
@Composable
fun FintrackApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination
    val items = MainTab.entries.map { FintrackBarItem(stringResource(it.labelRes), it.iconRes) }
    Box(modifier = modifier.fillMaxSize()) {
        FintrackNavHost(navController = navController, modifier = Modifier.fillMaxSize())
        FintrackBottomBar(
            items = items,
            selectedIndex = MainTab.entries.indexOfFirst { it.isSelectedIn(destination) },
            callbacks = barCallbacks(navController),
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

// DOC-09: a tab tap switches the NavHost destination; the add sheet (S-04) is wired with feature:transaction in P4
private fun barCallbacks(navController: NavHostController): FintrackBottomBarCallbacks =
    FintrackBottomBarCallbacks(
        onSelect = { index -> MainTab.entries[index].navigateFrom(navController) },
        onAddClick = {},
    )
