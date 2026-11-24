package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.ui.shortcut.ShortcutManageActions
import com.meqlause.fintrack.core.ui.shortcut.ShortcutManageList
import com.meqlause.fintrack.core.ui.R as UiR

// FR-039, S-03, DOC-08: the "Atur" drawer; a row edits, the trash deletes and undo shows right under the list
@Composable
internal fun HomeShortcutDrawer(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
) {
    PickerDrawer(
        title = stringResource(UiR.string.shortcut_manage_title),
        onClose = callbacks.onCloseDrawer,
    ) {
        ShortcutManageList(
            items = state.shortcuts,
            actions =
                ShortcutManageActions(
                    onDelete = callbacks.onDeleteShortcut,
                    onNew = {
                        callbacks.onCloseDrawer()
                        callbacks.onNewShortcut()
                    },
                    onEdit = {
                        callbacks.onCloseDrawer()
                        callbacks.onEditShortcut(it)
                    },
                ),
        )
        HomeShortcutUndo(state = state, callbacks = callbacks)
    }
}
