package com.meqlause.fintrack.feature.inbox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.component.ScreenBackHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-07: Kotak Masuk; the list, the Selesai section and the undo bar are drawn from one state
@Composable
internal fun InboxScreen(
    state: InboxUiState,
    actions: InboxActions,
    modifier: Modifier = Modifier,
    showInfo: Boolean = true,
) {
    val snackbar = remember { SnackbarHostState() }
    var picker by remember { mutableStateOf<InboxPicker?>(null) }
    var infoShown by rememberSaveable { mutableStateOf(showInfo) }
    var resolvedOpen by rememberSaveable { mutableStateOf(false) }
    UndoEffect(state.undoItemId, snackbar, actions)
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = FintrackTheme.colors.surfaces.background,
        snackbarHost = { SnackbarHost(snackbar) },
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            InboxHeader(onBack = actions.onBack, onDismissAll = actions.onDismissAll)
            InboxBody(
                state = state,
                actions = actions,
                infoShown = infoShown,
                onInfoDismiss = { infoShown = false },
                resolvedOpen = resolvedOpen,
                onToggleResolved = { resolvedOpen = !resolvedOpen },
                onPick = { picker = it },
            )
        }
    }
    picker?.let { InboxPickerDrawer(picker = it, state = state, actions = actions, onClose = { picker = null }) }
}

@Composable
private fun InboxHeader(
    onBack: () -> Unit,
    onDismissAll: () -> Unit,
) {
    ScreenBackHeader(
        title = stringResource(R.string.inbox_title),
        backLabel = stringResource(R.string.inbox_back_description),
        onBack = onBack,
        trailing = {
            TextButton(onClick = onDismissAll) {
                Text(
                    text = stringResource(R.string.inbox_dismiss_all),
                    style = FintrackScreenText.style(size = 12.5f, weight = 600),
                    color = FintrackTheme.colors.brands.primary,
                )
            }
        },
    )
}

// FR-087: the undo bar stays for the snackbar's time; Batalkan restores the dismissed item
@Composable
private fun UndoEffect(
    undoItemId: Long?,
    snackbar: SnackbarHostState,
    actions: InboxActions,
) {
    val message = stringResource(R.string.inbox_undo_message)
    val label = stringResource(R.string.inbox_undo_action)
    LaunchedEffect(undoItemId) {
        if (undoItemId == null) return@LaunchedEffect
        val result = snackbar.showSnackbar(message = message, actionLabel = label, withDismissAction = false)
        if (result == SnackbarResult.ActionPerformed) actions.onUndo() else actions.onUndoShown()
    }
}

private const val BACK_ROTATION = 180f
