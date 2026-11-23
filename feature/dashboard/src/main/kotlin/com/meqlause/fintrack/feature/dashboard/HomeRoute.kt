package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-040..043: stateful entry of S-03; the only composable that touches HomeViewModel
@Composable
internal fun HomeRoute(
    host: HomeHostLinks,
    entry: HomeEntryLinks,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        callbacks =
            HomeCallbacks(
                host = host,
                entry = entry,
                screen =
                    HomeCallbacks.Screen(
                        onToggleTheme = viewModel::toggleTheme,
                        onToggleHidden = viewModel::toggleHidden,
                        onOpenSummary = viewModel::openSummary,
                        onOpenCategories = viewModel::openCategories,
                        onCloseDrawer = viewModel::closeDrawer,
                        onOpenShortcuts = viewModel::openShortcuts,
                    ),
                shortcuts =
                    HomeCallbacks.Shortcuts(
                        onDeleteShortcut = viewModel::deleteShortcut,
                        onRestoreShortcut = viewModel::restoreShortcut,
                    ),
            ),
        modifier = modifier,
    )
}
