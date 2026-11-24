package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.core.ui.notification.NotificationAccessGate

// DOC-03, FR-081, FR-082, FR-090: stateful entry of S-13; the only composable that touches MonitoredAppsViewModel
@Composable
internal fun MonitoredAppsRoute(onBack: () -> Unit) {
    val viewModel: MonitoredAppsViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.refreshAccess()
        onPauseOrDispose {}
    }
    // DOC-10: the access row shows the disclosure first; only "Setuju" opens the system settings
    NotificationAccessGate { requestAccess ->
        MonitoredAppsScreen(
            state = state,
            callbacks =
                MonitoredAppsCallbacks(
                    onBack = onBack,
                    onOpenAccessSettings = requestAccess,
                    onToggle = viewModel::setEnabled,
                    onOpenPicker = viewModel::openPicker,
                    onPickWallet = viewModel::pickWallet,
                    onDismissPicker = viewModel::dismissPicker,
                ),
        )
    }
}
