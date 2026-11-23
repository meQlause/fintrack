package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.ui.shortcut.ShortcutManageList
import com.meqlause.fintrack.core.ui.R as UiR

// FR-039, S-03: the shortcut manager drawer opened from "Atur"; deleting here offers undo in Beranda
@Composable
internal fun HomeShortcutDrawer(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
) {
    PickerDrawer(
        title = stringResource(UiR.string.shortcut_manage_title),
        onClose = callbacks.onCloseDrawer,
    ) {
        ShortcutManageList(items = state.shortcuts, onDelete = callbacks.onDeleteShortcut)
    }
}
