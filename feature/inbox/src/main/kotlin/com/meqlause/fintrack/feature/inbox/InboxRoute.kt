package com.meqlause.fintrack.feature.inbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.core.ui.notification.NotificationAccessGate

// DOC-03, FR-084 … FR-091, S-07: stateful entry of Kotak Masuk; the only composable that touches InboxViewModel
@Composable
internal fun InboxRoute(onBack: () -> Unit) {
    val viewModel: InboxViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    // DOC-10: the access button shows the disclosure first; only "Setuju" opens the system settings
    NotificationAccessGate { requestAccess ->
        val actions =
            InboxActions(
                onBack = onBack,
                onDismissAll = viewModel::dismissEverything,
                onUndo = viewModel::undoDismiss,
                onUndoShown = viewModel::clearUndo,
                onEnableAccess = requestAccess,
                items =
                    InboxItemActions(
                        onConfirm = viewModel::confirmCard,
                        onDismiss = viewModel::dismissCard,
                        onConfirmPair = viewModel::confirmPair,
                        onSplitPair = viewModel::splitPair,
                        onChooseWallet = viewModel::chooseWallet,
                        onChooseCategory = viewModel::chooseCategory,
                    ),
            )
        InboxScreen(state = state, actions = actions)
    }
}
