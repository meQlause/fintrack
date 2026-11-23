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
            creditCallbacks(
                viewModel,
                onBack,
                CreditDetailCallbacks.Plans(onOpenInstalment, onOpenInstalments),
            ),
        modifier = modifier,
    )
}

// FR-131 to FR-138: the view model's actions and the host's links gathered for the screen
private fun creditCallbacks(
    viewModel: CreditDetailViewModel,
    onBack: () -> Unit,
    plans: CreditDetailCallbacks.Plans,
): CreditDetailCallbacks =
    CreditDetailCallbacks(
        onBack = onBack,
        bill =
            CreditDetailCallbacks.Bill(
                onOpenPay = viewModel::openPay,
                onDismissPay = viewModel::dismissPay,
                onPay = viewModel::payBill,
            ),
        entry =
            CreditDetailCallbacks.Entry(
                onAmountChange = viewModel::setPayAmount,
                onPickSource = viewModel::pickSource,
                onFillRemaining = viewModel::fillRemaining,
                onClearEntry = viewModel::clearAmount,
            ),
        days =
            CreditDetailCallbacks.Days(
                onOpenPicker = viewModel::openPicker,
                onDismissPicker = viewModel::dismissPicker,
                onPickDay = viewModel::pickDay,
            ),
        plans = plans,
    )
