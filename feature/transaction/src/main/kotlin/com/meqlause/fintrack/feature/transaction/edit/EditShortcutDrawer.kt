package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.shortcut.ShortcutGrid
import com.meqlause.fintrack.core.ui.shortcut.ShortcutManageList
import com.meqlause.fintrack.core.ui.shortcut.ShortcutTileMapping
import com.meqlause.fintrack.core.ui.shortcut.ShortcutTileParams
import com.meqlause.fintrack.core.ui.R as UiR

// FR-039, S-04: the "⚡ Pintasan" drawer (3-column grid) and its "Atur pintasan" manage view
@Composable
internal fun EditShortcutDrawer(
    state: EditTransactionUiState,
    sheet: EditSheet,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val manage = sheet == EditSheet.SHORTCUT_MANAGE
    val items: List<ShortcutTileParams> = ShortcutTileMapping.toTileParams(state.shortcuts, state.walletNames())
    PickerDrawer(
        title = stringResource(if (manage) UiR.string.shortcut_manage_title else UiR.string.shortcut_drawer_title),
        onClose = { onEvent(EditTransactionEvent.SheetDismissed) },
        modifier = modifier,
    ) {
        if (manage) {
            ShortcutManageList(
                items = items,
                onDelete = { onEvent(EditTransactionEvent.ShortcutDeleted(it)) },
            )
        } else {
            ShortcutGrid(items = items, onPick = { onEvent(EditTransactionEvent.ShortcutPicked(it)) })
            TextButton(onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.SHORTCUT_MANAGE)) }) {
                Text(text = stringResource(UiR.string.shortcut_manage_title))
            }
        }
    }
}

// FR-039: wallet names for the tiles, keyed by wallet id
private fun EditTransactionUiState.walletNames(): Map<WalletId, String> =
    catalog.wallets.associate { it.walletId to it.profile.name }
