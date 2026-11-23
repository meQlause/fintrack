package com.meqlause.fintrack.feature.credit.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// FR-131, S-14: stateful entry of the credit wallet detail; the only composable that touches the view model
@Composable
internal fun CreditDetailRoute(
    onBack: () -> Unit,
    onOpenInstalment: (Long) -> Unit,
    onOpenInstalments: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CreditDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    CreditDetailScreen(
        state = state,
        callbacks =
            CreditDetailCallbacks(
                onBack = onBack,
                onOpenPay = viewModel::openPay,
                onDismissPay = viewModel::dismissPay,
                onAmountChange = viewModel::setPayAmount,
                onPickSource = viewModel::pickSource,
                onFillRemaining = viewModel::fillRemaining,
                onClearEntry = viewModel::clearAmount,
                onPay = viewModel::payBill,
                onOpenPicker = viewModel::openPicker,
                onDismissPicker = viewModel::dismissPicker,
                onPickDay = viewModel::pickDay,
                onOpenInstalment = onOpenInstalment,
                onOpenInstalments = onOpenInstalments,
            ),
        modifier = modifier,
    )
}
