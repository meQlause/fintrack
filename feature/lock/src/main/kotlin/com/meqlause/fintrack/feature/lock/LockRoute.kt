package com.meqlause.fintrack.feature.lock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// FR-100, FR-101, S-02: stateful entry of the lock screen; onUnlocked runs once the PIN or biometrics unlock it
@Composable
fun LockRoute(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LockViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activity = LocalContext.current.findFragmentActivity()
    val title = stringResource(R.string.lock_biometric_title)
    val negative = stringResource(R.string.lock_biometric_negative)
    val showPrompt = {
        activity?.let { showBiometricPrompt(it, title, negative, viewModel::onBiometricSucceeded) }
        Unit
    }
    LaunchedEffect(state.unlocked) {
        if (state.unlocked) onUnlocked()
    }
    LaunchedEffect(state.biometricEnabled) {
        if (state.biometricEnabled) showPrompt()
    }
    LockScreen(
        state = state,
        keypad = PinKeypadCallbacks(onDigit = viewModel::onDigit, onBackspace = viewModel::onBackspace),
        onBiometric = showPrompt,
        modifier = modifier,
    )
}
