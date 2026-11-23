package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-036, S-05: stateful entry of Riwayat; the only composable that touches HistoryViewModel
@Composable
internal fun HistoryRoute(onOpenTransaction: (Long) -> Unit) {
    val viewModel: HistoryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    HistoryScreen(state = state, onEvent = viewModel::onEvent, onOpenTransaction = onOpenTransaction)
}
