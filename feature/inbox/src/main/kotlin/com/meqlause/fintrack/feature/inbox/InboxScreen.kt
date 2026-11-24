package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.BackAction
import com.meqlause.fintrack.core.designsystem.component.HeaderTitle
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.component.UndoSlot
import com.meqlause.fintrack.core.designsystem.component.UndoWindow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import kotlinx.coroutines.delay

// S-07: Kotak Masuk; the list, the Selesai section and the undo bar are drawn from one state
@Composable
internal fun InboxScreen(
    state: InboxUiState,
    actions: InboxActions,
    modifier: Modifier = Modifier,
    showInfo: Boolean = true,
) {
    var picker by remember { mutableStateOf<InboxPicker?>(null) }
    var infoShown by rememberSaveable { mutableStateOf(showInfo) }
    var resolvedOpen by rememberSaveable { mutableStateOf(false) }
    UndoEffect(state.undoItemId, actions)
    val panels =
        InboxPanels(
            infoShown = infoShown,
            resolvedOpen = resolvedOpen,
            onInfoDismiss = { infoShown = false },
            onToggleResolved = { resolvedOpen = !resolvedOpen },
            onPick = { picker = it },
        )
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FintrackTheme.colors.surfaces.background,
        snackbarHost = { InboxUndo(undoItemId = state.undoItemId, actions = actions) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            InboxHeader(
                onBack = actions.onBack,
                onDismissAll = actions.onDismissAll.takeIf { state.canDismissAll },
            )
            InboxBody(state = state, actions = actions, panels = panels)
        }
    }
    picker?.let { InboxPickerDrawer(picker = it, state = state, actions = actions, onClose = { picker = null }) }
}

// FR-087: the undo bar under the list; a new dismissal restarts its countdown
@Composable
private fun InboxUndo(
    undoItemId: Long?,
    actions: InboxActions,
) {
    UndoSlot(
        removed = undoItemId,
        message = stringResource(R.string.inbox_undo_message),
        onUndo = actions.onUndo,
        modifier = Modifier.padding(horizontal = FintrackScreenSizes.screenGutter),
    )
}

@Composable
private fun InboxHeader(
    onBack: () -> Unit,
    onDismissAll: (() -> Unit)?,
) {
    ScreenBackHeader(
        title = HeaderTitle(text = stringResource(R.string.inbox_title)),
        back = BackAction(label = stringResource(R.string.inbox_back_description), onBack = onBack),
        trailing = {
            // S-07: Abaikan semua only shows while something waits to be dismissed
            if (onDismissAll != null) {
                TextButton(onClick = onDismissAll) {
                    Text(
                        text = stringResource(R.string.inbox_dismiss_all),
                        style = FintrackScreenText.style(size = 12.5f, weight = 600),
                        color = FintrackTheme.colors.brands.primary,
                    )
                }
            }
        },
    )
}

// FR-087: the undo window ends by itself; a new dismissal restarts it, and Urungkan restores the item
@Composable
private fun UndoEffect(
    undoItemId: Long?,
    actions: InboxActions,
) {
    LaunchedEffect(undoItemId) {
        if (undoItemId == null) return@LaunchedEffect
        delay(UndoWindow.MILLIS)
        actions.onUndoShown()
    }
}
