package com.meqlause.fintrack.core.ui.shortcut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.QuickEntryId

private const val COLUMNS = 3

// FR-039, S-04: the shortcut drawer's 3-column grid; picking a tile fills the sheet and closes the drawer
@Composable
fun ShortcutGrid(
    items: List<ShortcutTileParams>,
    onPick: (QuickEntryId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        items.chunked(COLUMNS).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
                row.forEach { params ->
                    ShortcutTile(
                        params = params,
                        onClick = { onPick(params.quickEntryId) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
