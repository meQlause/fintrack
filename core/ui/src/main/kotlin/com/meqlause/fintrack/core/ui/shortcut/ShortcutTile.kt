package com.meqlause.fintrack.core.ui.shortcut

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.icon.IconCircle
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter

// FR-039, S-03, S-04: one shortcut as a tile; a tap is handed to the host and saves nothing (BR-047)
@Composable
fun ShortcutTile(
    params: ShortcutTileParams,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(FintrackTheme.shapes.medium)
                .background(FintrackTheme.colors.surfaces.surfaceVariant)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(FintrackTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xxSmall),
    ) {
        IconCircle(iconKey = params.appearance.iconKey, colorKey = params.appearance.colorKey)
        Text(
            text = params.title,
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = MoneyFormatter.format(params.amount),
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            maxLines = 1,
        )
        Text(
            text = params.walletName,
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.faint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
