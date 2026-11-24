package com.meqlause.fintrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.meqlause.fintrack.core.designsystem.component.LocalSheetCover
import com.meqlause.fintrack.core.designsystem.component.SheetCover
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.edit.navigation.EditTransactionDestination
import com.meqlause.fintrack.lock.AppLockGate
import com.meqlause.fintrack.lock.LockOverlay
import com.meqlause.fintrack.navigation.FintrackNavHost
import com.meqlause.fintrack.navigation.MainTab
import com.meqlause.fintrack.startup.AppStartViewModel

// FR-001, FR-102, DOC-09: the app opens on onboarding when it is needed, otherwise on Beranda; the lock covers it all
@Composable
fun FintrackApp(
    lockGate: AppLockGate,
    modifier: Modifier = Modifier,
    startViewModel: AppStartViewModel = hiltViewModel(),
) {
    val showOnboarding by startViewModel.showOnboarding.collectAsStateWithLifecycle()
    val locked by lockGate.isLocked.collectAsStateWithLifecycle()
    Box(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background)) {
        showOnboarding?.let { onboardingNeeded ->
            FintrackShell(onboardingNeeded = onboardingNeeded, modifier = Modifier.fillMaxSize())
        }
        LockOverlay(locked = locked, onUnlocked = lockGate::unlock, modifier = Modifier.fillMaxSize())
    }
}

// DOC-09: the NavHost fills the screen and the floating bottom bar sits over it, shown on the four main tabs only
@Composable
private fun FintrackShell(
    onboardingNeeded: Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val cover = remember { SheetCover() }
    CompositionLocalProvider(LocalSheetCover provides cover) {
        Box(modifier = modifier.fillMaxSize()) {
            // NFR-010: Android 15+ draws edge to edge, so content starts below the status bar
            FintrackNavHost(
                navController = navController,
                onboardingNeeded = onboardingNeeded,
                modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars),
            )
            ShellBar(navController = navController, isCovered = cover.isCovered)
        }
    }
}

// DOC-09: the bottom bar, hidden on other screens and while a sheet covers the tab it floats over
@Composable
private fun BoxScope.ShellBar(
    navController: NavHostController,
    isCovered: Boolean,
) {
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination
    if (MainTab.showsBarOn(destination) && !isCovered) {
        FintrackBottomBar(
            items = MainTab.entries.map { FintrackBarItem(stringResource(it.labelRes), it.iconRes) },
            selectedIndex = MainTab.entries.indexOfFirst { it.isSelectedIn(destination) },
            callbacks = barCallbacks(navController),
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding(),
        )
    }
}

// DOC-09: a tab tap switches the NavHost destination; the centre button opens the add sheet (S-04)
private fun barCallbacks(navController: NavHostController): FintrackBottomBarCallbacks =
    FintrackBottomBarCallbacks(
        onSelect = { index -> MainTab.entries[index].navigateFrom(navController) },
        onAddClick = { navController.navigate(EditTransactionDestination()) },
    )
