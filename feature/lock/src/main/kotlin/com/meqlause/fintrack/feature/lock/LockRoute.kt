package com.meqlause.fintrack.feature.lock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// FR-100, S-02: stateful entry of the lock screen; onUnlocked runs once the correct PIN is entered
@Composable
fun LockRoute(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LockViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.unlocked) {
        if (state.unlocked) onUnlocked()
    }
    LockScreen(
        state = state,
        onDigit = viewModel::onDigit,
        onBackspace = viewModel::onBackspace,
        modifier = modifier,
    )
}
