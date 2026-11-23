package com.meqlause.fintrack.feature.investment.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.feature.investment.AmountSheetCallbacks

// DOC-03, FR-173: stateful entry of S-20; the only composable that touches InvestmentDetailViewModel
@Composable
public fun InvestmentDetailRoute(
    onBack: () -> Unit,
    onTransfer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: InvestmentDetailViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    InvestmentDetailScreen(
        state = state,
        callbacks =
            InvestmentDetailCallbacks(
                onBack = onBack,
                onOpenValuation = viewModel::openValuation,
                onTransfer = onTransfer,
                valuation =
                    AmountSheetCallbacks(
                        onDigit = viewModel::typeDigit,
                        onBackspace = viewModel::backspace,
                        onSave = viewModel::save,
                        onDismiss = viewModel::dismissValuation,
                    ),
            ),
        modifier = modifier,
    )
}
