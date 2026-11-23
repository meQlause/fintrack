package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-030..FR-034: stateful entry of S-04; the only composable that touches EditTransactionViewModel
@Composable
internal fun EditTransactionRoute(
    transactionId: Long?,
    onDismiss: () -> Unit,
) {
    val viewModel: EditTransactionViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(transactionId) { viewModel.open(transactionId) }
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onDismiss()
    }
    EditTransactionScreen(state = state, onEvent = viewModel::onEvent, onDismiss = onDismiss)
}
