package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.UndoSnackbar
import com.meqlause.fintrack.core.ui.R as UiR

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
        sheetState = rememberBottomSheetState(SheetValue.Hidden, setOf(SheetValue.Hidden, SheetValue.Expanded)),
        dragHandle = null,
        containerColor = Color.Transparent,
    ) {
        FintrackBottomSheet(modifier = modifier) {
            Column(modifier = Modifier.fillMaxWidth()) {
                EditTransactionSheetContent(state = state, onEvent = onEvent)
                state.removedShortcut?.let {
                    UndoSnackbar(
                        message = stringResource(UiR.string.shortcut_deleted),
                        onUndo = { onEvent(EditTransactionEvent.ShortcutRestored) },
                    )
                }
            }
        }
    }
}

// S-04: the form, or the wallet or shortcut drawer that replaces it while one is open
@Composable
private fun EditTransactionSheetContent(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
) {
    when (val openSheet = state.openSheet) {
        null -> {
            EditTransactionFormBody(state = state, onEvent = onEvent)
        }

        EditSheet.SHORTCUTS, EditSheet.SHORTCUT_MANAGE -> {
            EditShortcutDrawer(state = state, sheet = openSheet, onEvent = onEvent)
        }

        EditSheet.WALLET, EditSheet.TARGET_WALLET -> {
            EditTransactionWalletPicker(state = state, sheet = openSheet, onEvent = onEvent)
        }

        EditSheet.TENOR -> {
            EditInstalmentTenorDrawer(state = state, onEvent = onEvent)
        }

        EditSheet.LOAN_DUE -> {
            EditLoanDueDrawer(onEvent = onEvent)
        }
    }
}
