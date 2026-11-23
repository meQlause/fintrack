package com.meqlause.fintrack.feature.inbox

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-084 … FR-091, S-07: stateful entry of Kotak Masuk; the only composable that touches InboxViewModel
@Composable
internal fun InboxRoute(onBack: () -> Unit) {
    val viewModel: InboxViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val actions =
        InboxActions(
            onBack = onBack,
            onConfirm = viewModel::confirmCard,
            onDismiss = viewModel::dismissCard,
            onUndo = viewModel::undoDismiss,
            onUndoShown = viewModel::clearUndo,
            onDismissAll = viewModel::dismissEverything,
            onConfirmPair = viewModel::confirmPair,
            onSplitPair = viewModel::splitPair,
            onChooseWallet = viewModel::chooseWallet,
            onChooseCategory = viewModel::chooseCategory,
            onEnableAccess = {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            },
        )
    InboxScreen(state = state, actions = actions)
}
