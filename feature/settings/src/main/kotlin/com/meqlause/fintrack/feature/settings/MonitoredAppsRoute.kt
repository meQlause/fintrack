package com.meqlause.fintrack.feature.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-081, FR-082, FR-090: stateful entry of S-13; the only composable that touches MonitoredAppsViewModel
@Composable
internal fun MonitoredAppsRoute(onBack: () -> Unit) {
    val viewModel: MonitoredAppsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LifecycleResumeEffect(Unit) {
        viewModel.refreshAccess()
        onPauseOrDispose {}
    }
    MonitoredAppsScreen(
        state = state,
        callbacks =
            MonitoredAppsCallbacks(
                onBack = onBack,
                onOpenAccessSettings = {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                },
                onToggle = viewModel::setEnabled,
                onOpenPicker = viewModel::openPicker,
                onPickWallet = viewModel::pickWallet,
                onDismissPicker = viewModel::dismissPicker,
            ),
    )
}
