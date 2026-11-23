package com.meqlause.fintrack.core.ui.shortcut

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-039: the "Atur pintasan" list; every row can be deleted, and the host offers undo for the last delete
@Composable
fun ShortcutManageList(
    items: List<ShortcutTileParams>,
    onDelete: (QuickEntryId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        if (items.isEmpty()) {
            Text(
                text = stringResource(R.string.shortcut_empty),
                style = FintrackTheme.typography.bodyMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
        items.forEach { params -> ShortcutManageRow(params = params, onDelete = onDelete) }
    }
}

@Composable
private fun ShortcutManageRow(
    params: ShortcutTileParams,
    onDelete: (QuickEntryId) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShortcutLabels(params = params, modifier = Modifier.weight(1f))
        IconButton(onClick = { onDelete(params.quickEntryId) }) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_close),
                contentDescription = stringResource(R.string.shortcut_delete_description),
                tint = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.padding(FintrackTheme.spacing.xxSmall),
            )
        }
    }
}

@Composable
private fun ShortcutLabels(
    params: ShortcutTileParams,
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = params.title,
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "${params.walletName} · ${MoneyFormatter.format(params.amount)}",
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
