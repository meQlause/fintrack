package com.meqlause.fintrack.core.ui.shortcut

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

// FR-039, S-03, S-04, DOC-08: one shortcut as the mockup .ptile (icon box, title, compact amount); a tap saves nothing
@Composable
fun ShortcutTile(
    params: ShortcutTileParams,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = FintrackTheme.shapes.medium
    Column(
        modifier =
            modifier
                .clip(shape)
                .background(FintrackTheme.colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, FintrackTheme.colors.surfaces.outline, shape)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(FintrackTheme.spacing.medium - FintrackTheme.spacing.xxSmall),
    ) {
        IconCircle(
            iconKey = params.appearance.iconKey,
            colorKey = params.appearance.colorKey,
            size = FintrackTheme.sizes.shortcutIcon,
            glyph = FintrackTheme.sizes.shortcutGlyph,
            shape = FintrackTheme.shapes.shortcutIcon,
        )
        Text(
            text = params.title,
            modifier = Modifier.padding(top = FintrackTheme.spacing.small),
            style = FintrackTheme.typography.captionWide,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = MoneyFormatter.formatShort(params.amount),
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall / 2),
            style = FintrackTheme.typography.numberCompact,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
        )
    }
}
