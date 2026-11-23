package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet

// FR-030..FR-034, S-04: the add or edit sheet; stateless, so previews and UI tests drive it with plain values
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditTransactionScreen(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        containerColor = Color.Transparent,
    ) {
        FintrackBottomSheet(modifier = modifier) {
            val openSheet = state.openSheet
            if (openSheet == null) {
                EditTransactionFormBody(state = state, onEvent = onEvent)
            } else {
                EditTransactionWalletPicker(state = state, sheet = openSheet, onEvent = onEvent)
            }
        }
    }
}
