package com.meqlause.fintrack.feature.investment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.core.model.WalletId

// DOC-03, FR-173: stateful entry of the Investasi tab; the only composable that touches InvestmentViewModel
@Composable
public fun InvestmentRoute(
    onOpenAccount: (WalletId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: InvestmentViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    InvestmentScreen(
        state = state,
        callbacks =
            InvestmentCallbacks(
                onAdd = viewModel::startCreate,
                onOpenAccount = onOpenAccount,
                onNameChange = viewModel::changeName,
                onOpenKindPicker = viewModel::openKindPicker,
                onPickKind = viewModel::pickKind,
                sheet =
                    AmountSheetCallbacks(
                        onDigit = viewModel::typeDigit,
                        onBackspace = viewModel::backspace,
                        onSave = viewModel::save,
                        onDismiss = viewModel::dismissDraft,
                    ),
            ),
        modifier = modifier,
    )
}
