package com.meqlause.fintrack.feature.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-020: stateful entry of S-10; the only composable that touches CategoryViewModel
@Composable
public fun CategoryRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CategoryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    CategoryScreen(
        modifier = modifier,
        state = state,
        callbacks = categoryCallbacks(viewModel = viewModel, onBack = onBack),
    )
}

// S-10: wires every screen action to the matching ViewModel function
private fun categoryCallbacks(
    viewModel: CategoryViewModel,
    onBack: () -> Unit,
): CategoryCallbacks =
    CategoryCallbacks(
        onBack = onBack,
        onSelectKind = viewModel::selectKind,
        onAdd = viewModel::startCreate,
        list =
            CategoryListCallbacks(
                onEdit = viewModel::startEdit,
                onMoveUp = { viewModel.move(it, upward = true) },
                onMoveDown = { viewModel.move(it, upward = false) },
                onSetArchived = viewModel::setArchived,
                onDelete = viewModel::requestDelete,
            ),
        editor =
            CategoryEditorCallbacks(
                onNameChange = viewModel::changeName,
                onIconChange = viewModel::changeIcon,
                onColorChange = viewModel::changeColor,
                onSave = viewModel::saveEditor,
                onDismissEditor = viewModel::dismissEditor,
            ),
        archiveOffer =
            ArchiveOfferCallbacks(
                onConfirm = viewModel::confirmArchiveOffer,
                onDismiss = viewModel::dismissArchiveOffer,
            ),
    )
