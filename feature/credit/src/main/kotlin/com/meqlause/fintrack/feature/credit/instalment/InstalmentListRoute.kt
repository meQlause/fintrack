package com.meqlause.fintrack.feature.credit.instalment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// FR-142, S-16: stateful entry of the instalment list; the only composable that touches its view model
@Composable
internal fun InstalmentListRoute(
    onBack: () -> Unit,
    onOpenInstalment: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: InstalmentListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    InstalmentListScreen(
        state = state,
        callbacks =
            InstalmentListCallbacks(
                onBack = onBack,
                onPickFilter = viewModel::pickFilter,
                onOpenInstalment = onOpenInstalment,
            ),
        modifier = modifier,
    )
}
