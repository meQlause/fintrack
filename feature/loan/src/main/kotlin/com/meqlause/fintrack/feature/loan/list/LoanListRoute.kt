package com.meqlause.fintrack.feature.loan.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// FR-153, S-17: stateful entry of the loan list; the only composable that touches its view model
@Composable
internal fun LoanListRoute(
    onBack: () -> Unit,
    onOpenLoan: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LoanListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val callbacks =
        LoanListCallbacks(
            onBack = onBack,
            onPickDirection = viewModel::pickDirection,
            onOpenLoan = onOpenLoan,
        )
    LoanListScreen(state = state, callbacks = callbacks, modifier = modifier)
}
