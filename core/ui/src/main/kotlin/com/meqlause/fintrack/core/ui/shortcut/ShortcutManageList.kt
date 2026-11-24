package com.meqlause.fintrack.core.ui.shortcut

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.component.ScreenDashedButton
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.list.AnimatedRows

// FR-039, DOC-08: the "Atur pintasan" body, one card of shortcut rows over the dashed "Pintasan baru";
// a deleted row shrinks away at once and the host offers undo
@Composable
fun ShortcutManageList(
    items: List<ShortcutTileParams>,
    actions: ShortcutManageActions,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.shortcut_empty),
                style = FintrackScreenText.style(size = 13f, weight = 400),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = FintrackTheme.spacing.xSmall),
            )
        } else {
            ScreenCard {
                AnimatedRows(items = items, keyOf = { it.quickEntryId }) { params ->
                    val first = params.quickEntryId == items.firstOrNull()?.quickEntryId
                    ShortcutManageRow(params = params, actions = actions, showDivider = !first)
                }
            }
        }
        actions.onNew?.let { onNew ->
            ScreenDashedButton(
                text = stringResource(R.string.shortcut_new),
                onClick = onNew,
                modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
            )
        }
    }
}
