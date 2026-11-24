package com.meqlause.fintrack.feature.transaction.shortcut

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.LocalSheetPortal

// FR-039, S-03: the "Pintasan baru" sheet; stateless, so previews and UI tests drive it with plain values
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShortcutFormScreen(
    state: ShortcutFormUiState,
    onEvent: (ShortcutFormEvent) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // DOC-08: this screen is its own modal window, so its drawers stay inside it
    CompositionLocalProvider(LocalSheetPortal provides null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberBottomSheetState(SheetValue.Hidden, setOf(SheetValue.Hidden, SheetValue.Expanded)),
            dragHandle = null,
            containerColor = Color.Transparent,
        ) {
            FintrackBottomSheet(modifier = modifier) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ShortcutFormContent(state = state, onEvent = onEvent, onClose = onDismiss)
                }
            }
        }
    }
}

// FR-039: the form, or the category or wallet drawer that replaces it while one is open
@Composable
private fun ShortcutFormContent(
    state: ShortcutFormUiState,
    onEvent: (ShortcutFormEvent) -> Unit,
    onClose: () -> Unit,
) {
    when (state.openSheet) {
        null -> ShortcutFormBody(state = state, onEvent = onEvent, onClose = onClose)
        ShortcutSheet.CATEGORY -> ShortcutCategoryDrawer(state = state, onEvent = onEvent)
        ShortcutSheet.WALLET -> ShortcutWalletDrawer(state = state, onEvent = onEvent)
    }
}
