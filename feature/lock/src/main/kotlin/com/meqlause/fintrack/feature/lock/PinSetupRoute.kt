package com.meqlause.fintrack.feature.lock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// FR-100, S-01, S-12: stateful entry of the PIN setup; onDone runs once the PIN is stored or removed
@Composable
internal fun PinSetupRoute(
    onDone: () -> Unit,
    onCancel: () -> Unit,
) {
    val viewModel: PinSetupViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.done) {
        if (state.done) onDone()
    }
    PinSetupScreen(
        state = state,
        onDigit = viewModel::onDigit,
        onBackspace = viewModel::onBackspace,
        onCancel = onCancel,
    )
}
