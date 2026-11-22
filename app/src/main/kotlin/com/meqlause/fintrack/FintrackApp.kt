package com.meqlause.fintrack

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meqlause.fintrack.core.designsystem.component.FintrackBarItem
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomBar
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomBarCallbacks
import com.meqlause.fintrack.feature.onboarding.OnboardingDestination
import com.meqlause.fintrack.navigation.FintrackNavHost
import com.meqlause.fintrack.navigation.MainTab
import com.meqlause.fintrack.startup.AppStartViewModel

// FR-001, DOC-09: the app opens on onboarding when it is needed, otherwise on Beranda; the shell waits for that answer
@Composable
fun FintrackApp(
    modifier: Modifier = Modifier,
    startViewModel: AppStartViewModel = hiltViewModel(),
) {
    val showOnboarding by startViewModel.showOnboarding.collectAsStateWithLifecycle()
    showOnboarding?.let { onboardingNeeded ->
        FintrackShell(onboardingNeeded = onboardingNeeded, modifier = modifier)
    }
}

// DOC-09: the NavHost fills the screen and the floating bottom bar sits over it, hidden during onboarding
@Composable
private fun FintrackShell(
    onboardingNeeded: Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination
    val items = MainTab.entries.map { FintrackBarItem(stringResource(it.labelRes), it.iconRes) }
    val isOnboardingShown = destination?.route == OnboardingDestination::class.java.name
    Box(modifier = modifier.fillMaxSize()) {
        FintrackNavHost(
            navController = navController,
            onboardingNeeded = onboardingNeeded,
            modifier = Modifier.fillMaxSize(),
        )
        if (destination != null && !isOnboardingShown) {
            FintrackBottomBar(
                items = items,
                selectedIndex = MainTab.entries.indexOfFirst { it.isSelectedIn(destination) },
                callbacks = barCallbacks(navController),
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

// DOC-09: a tab tap switches the NavHost destination; the add sheet (S-04) is wired with feature:transaction in P4
private fun barCallbacks(navController: NavHostController): FintrackBottomBarCallbacks =
    FintrackBottomBarCallbacks(
        onSelect = { index -> MainTab.entries[index].navigateFrom(navController) },
        onAddClick = {},
    )
