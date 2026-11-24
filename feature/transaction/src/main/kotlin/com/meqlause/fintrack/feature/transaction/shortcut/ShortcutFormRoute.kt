package com.meqlause.fintrack.feature.transaction.shortcut

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-039: stateful entry of the shortcut form; the only composable that touches ShortcutFormViewModel
@Composable
internal fun ShortcutFormRoute(
    quickEntryId: Long?,
    onDismiss: () -> Unit,
) {
    val viewModel: ShortcutFormViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(quickEntryId) { viewModel.open(quickEntryId) }
    LaunchedEffect(state.status.isSaved) {
        if (state.status.isSaved) onDismiss()
    }
    ShortcutFormScreen(state = state, onEvent = viewModel::onEvent, onDismiss = onDismiss)
}
