package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.UndoSlot
import com.meqlause.fintrack.core.ui.R as UiR

// FR-039, S-03: the undo bar under the manager list; it grows in at a delete and shrinks away after the window
@Composable
internal fun HomeShortcutUndo(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
) {
    UndoSlot(
        removed = state.removedShortcut,
        message = stringResource(UiR.string.shortcut_deleted),
        onUndo = callbacks.onRestoreShortcut,
    )
}
