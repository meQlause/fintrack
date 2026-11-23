package com.meqlause.fintrack.feature.transaction.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-034, FR-035, S-06: stateful entry of the detail; the only composable using TransactionDetailViewModel
@Composable
internal fun TransactionDetailRoute(
    transactionId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
) {
    val viewModel: TransactionDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.isClosed) {
        if (state.isClosed) onBack()
    }
    TransactionDetailScreen(
        state = state,
        onBack = onBack,
        onEdit = { onEdit(transactionId) },
        onDuplicate = viewModel::duplicate,
        onDelete = viewModel::requestDelete,
        onUndo = viewModel::undoDelete,
    )
}
