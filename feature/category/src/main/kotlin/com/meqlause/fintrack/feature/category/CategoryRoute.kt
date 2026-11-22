package com.meqlause.fintrack.feature.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// DOC-03, FR-020: stateful entry of S-10; the only composable that touches CategoryViewModel
@Composable
public fun CategoryRoute(onBack: () -> Unit) {
    val viewModel: CategoryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    CategoryScreen(
        state = state,
        callbacks =
            CategoryCallbacks(
                onBack = onBack,
                onSelectKind = viewModel::selectKind,
                onAdd = viewModel::startCreate,
                onEdit = viewModel::startEdit,
                onMoveUp = { viewModel.move(it, up = true) },
                onMoveDown = { viewModel.move(it, up = false) },
                onSetArchived = viewModel::setArchived,
                onDelete = viewModel::requestDelete,
                onNameChange = viewModel::changeName,
                onIconChange = viewModel::changeIcon,
                onColorChange = viewModel::changeColor,
                onSave = viewModel::saveEditor,
                onDismissEditor = viewModel::dismissEditor,
                onArchiveOfferConfirm = viewModel::confirmArchiveOffer,
                onArchiveOfferDismiss = viewModel::dismissArchiveOffer,
            ),
    )
}
