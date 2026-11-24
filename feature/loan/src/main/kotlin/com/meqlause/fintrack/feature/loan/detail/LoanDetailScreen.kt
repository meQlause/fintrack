package com.meqlause.fintrack.feature.loan.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.LocalSheetPortal
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.loan.R

// S-17: the loan detail screen; stateless, previews drive it with plain values
@Composable
internal fun LoanDetailScreen(
    state: LoanDetailUiState,
    onEvent: (LoanDetailEvent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(R.string.loan_back),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.brands.primary,
            modifier = Modifier.clickable(onClick = onBack).padding(vertical = FintrackTheme.spacing.small),
        )
        state.entry?.let { entry -> LoanDetailBody(entry = entry, state = state, onEvent = onEvent) }
    }
    LoanSheetHost(state = state, onEvent = onEvent)
    LoanDialogHost(state = state, onEvent = onEvent)
}

// S-17: the bottom sheet of the open sheet kind, the repayment keypad or the due-date options
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoanSheetHost(
    state: LoanDetailUiState,
    onEvent: (LoanDetailEvent) -> Unit,
) {
    val sheet = state.overlay.sheet ?: return
    // DOC-08: the modal window holds this sheet itself, so it is not drawn through the sheet portal
    CompositionLocalProvider(LocalSheetPortal provides null) {
        ModalBottomSheet(onDismissRequest = { onEvent(LoanDetailEvent.DismissSheet) }) {
            FintrackBottomSheet {
                when (sheet) {
                    LoanDetailSheet.REPAYMENT -> LoanRepaymentSheet(state = state, onEvent = onEvent)
                    LoanDetailSheet.DUE_DATE -> LoanDueSheet(onEvent = onEvent)
                }
            }
        }
    }
}

// FR-155, BR-046: the write-off and delete confirmations
@Composable
private fun LoanDialogHost(
    state: LoanDetailUiState,
    onEvent: (LoanDetailEvent) -> Unit,
) {
    val dialog = state.overlay.dialog ?: return
    LoanConfirmDialog(
        dialog = dialog,
        onConfirm = { onEvent(LoanDetailEvent.DialogConfirmed) },
        onDismiss = { onEvent(LoanDetailEvent.DialogDismissed) },
    )
}
