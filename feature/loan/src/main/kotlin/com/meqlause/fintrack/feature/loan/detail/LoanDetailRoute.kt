package com.meqlause.fintrack.feature.loan.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// FR-155, S-17: stateful entry of the loan detail; it leaves the screen once the loan is deleted
@Composable
internal fun LoanDetailRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LoanDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.finished) {
        if (state.finished) onBack()
    }
    LoanDetailScreen(state = state, onEvent = viewModel::onEvent, onBack = onBack, modifier = modifier)
}
