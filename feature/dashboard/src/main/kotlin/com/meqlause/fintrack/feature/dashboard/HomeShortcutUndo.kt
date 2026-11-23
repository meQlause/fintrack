package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.UndoSnackbar
import com.meqlause.fintrack.core.ui.R as UiR

// FR-039, S-03: the undo bar shown for five seconds after a shortcut is deleted from the manager
@Composable
internal fun HomeShortcutUndo(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
) {
    if (state.removedShortcut == null) return
    UndoSnackbar(
        message = stringResource(UiR.string.shortcut_deleted),
        onUndo = callbacks.onRestoreShortcut,
    )
}
