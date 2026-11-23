package com.meqlause.fintrack.feature.wallet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType

// DOC-03, FR-010: stateful entry of S-09; the only composable that touches WalletViewModel
@Composable
public fun WalletRoute(
    onOpenDetail: (WalletId, WalletType) -> Unit,
    onOpenLoans: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: WalletViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    WalletScreen(
        state = state,
        callbacks =
            walletCallbacks(
                viewModel,
                WalletCallbacks.ListLinks(onOpenDetail = onOpenDetail, onOpenLoans = onOpenLoans),
            ),
        modifier = modifier,
    )
}

// FR-010..016: the view model's actions and the host's links gathered for the screen
private fun walletCallbacks(
    viewModel: WalletViewModel,
    host: WalletCallbacks.ListLinks,
): WalletCallbacks =
    WalletCallbacks(
        list =
            host.copy(
                onAdd = viewModel::startCreate,
                onEdit = viewModel::startEdit,
                onToggleArchived = viewModel::toggleArchivedSection,
            ),
        fields =
            WalletCallbacks.Fields(
                onNameChange = { text -> viewModel.edit { it.withName(text) } },
                onInitialBalanceChange = { text -> viewModel.edit { it.withInitialBalance(text) } },
                onCreditLimitChange = { text -> viewModel.edit { it.withCreditLimit(text) } },
                onIncludeInTotalChange = { include -> viewModel.edit { it.withIncludeInTotal(include) } },
            ),
        picks =
            WalletCallbacks.Picks(
                onOpenPicker = viewModel::openPicker,
                onDismissPicker = viewModel::dismissPicker,
                onPickType = { type -> viewModel.pick { it.withType(type) } },
                onPickInvestKind = { kind -> viewModel.pick { it.withInvestKind(kind) } },
                onPickDay = viewModel::pickDay,
            ),
        sheet =
            WalletCallbacks.Sheet(
                onSetArchivedEditing = viewModel::setArchivedEditing,
                onSave = viewModel::save,
                onDismissEditor = viewModel::dismissEditor,
            ),
    )
