package com.meqlause.fintrack.feature.credit.instalment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// FR-144, FR-145, S-16: stateful entry of one plan; leaves the screen once the plan has been deleted
@Composable
internal fun InstalmentDetailRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: InstalmentDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state) {
        if (state == InstalmentDetailUiState.Closed) onBack()
    }
    InstalmentDetailScreen(
        state = state,
        callbacks =
            InstalmentDetailCallbacks(
                onBack = onBack,
                onPayOff = viewModel::payOff,
                onRequestDelete = viewModel::requestDelete,
                onDismissDelete = viewModel::dismissDelete,
                onConfirmDelete = viewModel::confirmDelete,
            ),
        modifier = modifier,
    )
}
