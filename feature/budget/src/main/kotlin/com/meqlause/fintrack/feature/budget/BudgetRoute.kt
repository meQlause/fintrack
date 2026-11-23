package com.meqlause.fintrack.feature.budget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-070: stateful entry of S-11; the only composable that touches BudgetViewModel
@Composable
public fun BudgetRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: BudgetViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    BudgetScreen(
        modifier = modifier,
        state = state,
        callbacks =
            BudgetCallbacks(
                onBack = onBack,
                onAdd = viewModel::startAdd,
                onEdit = viewModel::startEdit,
                onPick = viewModel::pick,
                editor =
                    BudgetEditorCallbacks(
                        onDigit = viewModel::typeDigit,
                        onBackspace = viewModel::backspace,
                        onSave = viewModel::save,
                        onDismissEditor = viewModel::dismissEditor,
                    ),
            ),
    )
}
