package com.meqlause.fintrack.feature.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.core.model.WalletId

// DOC-03, FR-010: stateful entry of S-09; the only composable that touches WalletViewModel
@Composable
public fun WalletRoute(
    onOpenDetail: (WalletId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: WalletViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    WalletScreen(
        state = state,
        callbacks =
            WalletCallbacks(
                onAdd = viewModel::startCreate,
                onOpenDetail = onOpenDetail,
                onEdit = viewModel::startEdit,
                onToggleArchived = viewModel::toggleArchivedSection,
                onNameChange = viewModel::changeName,
                onOpenPicker = viewModel::openPicker,
                onDismissPicker = viewModel::dismissPicker,
                onPickType = viewModel::pickType,
                onPickInvestKind = viewModel::pickInvestKind,
                onPickDay = viewModel::pickDay,
                onInitialBalanceChange = viewModel::changeInitialBalance,
                onCreditLimitChange = viewModel::changeCreditLimit,
                onIncludeInTotalChange = viewModel::changeIncludeInTotal,
                onSetArchivedEditing = viewModel::setArchivedEditing,
                onSave = viewModel::save,
                onDismissEditor = viewModel::dismissEditor,
            ),
        modifier = modifier,
    )
}
