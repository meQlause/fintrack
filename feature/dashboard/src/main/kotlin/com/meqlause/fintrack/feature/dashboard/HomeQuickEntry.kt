package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.ui.shortcut.ShortcutTile
import com.meqlause.fintrack.core.ui.shortcut.ShortcutTileParams
import com.meqlause.fintrack.core.designsystem.R as DesignR

private val DASH_INTERVALS = floatArrayOf(10f, 8f)

// DOC-09, S-03, FR-039: "Catat cepat" row of shortcut tiles, a dashed "Baru" tile and "Atur" for the manager drawer
@Composable
internal fun HomeQuickEntry(
    shortcuts: List<ShortcutTileParams>,
    onNewEntry: () -> Unit,
    onApplyShortcut: (QuickEntryId) -> Unit,
    onManage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.home_quick_title),
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onManage) {
                Text(text = stringResource(R.string.home_shortcut_manage))
            }
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()).height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            shortcuts.forEach { params ->
                ShortcutTile(
                    params = params,
                    onClick = { onApplyShortcut(params.quickEntryId) },
                    modifier = Modifier.width(FintrackTheme.sizes.shortcutTileWidth),
                )
            }
            NewShortcutTile(onClick = onNewEntry)
        }
    }
}

// FR-039: the dashed tile that opens a blank add sheet, as tall as the shortcut tiles beside it
@Composable
private fun NewShortcutTile(onClick: () -> Unit) {
    val dashColor = FintrackTheme.colors.surfaces.outline
    val stroke = FintrackTheme.sizes.hairline
    val corner = FintrackTheme.spacing.medium
    Column(
        modifier =
            Modifier
                .width(FintrackTheme.sizes.shortcutTileWidth)
                .fillMaxHeight()
                .drawBehind { drawDashedOutline(dashColor, stroke.toPx(), corner.toPx()) }
                .clip(FintrackTheme.shapes.medium)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(FintrackTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(DesignR.drawable.ic_plus),
            contentDescription = null,
            tint = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
        )
        Spacer(modifier = Modifier.height(FintrackTheme.spacing.xSmall))
        Text(
            text = stringResource(R.string.home_quick_add),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

// FR-039: the stroke is inset by half its width so the dashes are not cut by the tile edge
private fun DrawScope.drawDashedOutline(
    color: Color,
    strokeWidth: Float,
    cornerRadius: Float,
) {
    val half = strokeWidth / 2f
    drawRoundRect(
        color = color,
        topLeft = Offset(half, half),
        size = Size(size.width - strokeWidth, size.height - strokeWidth),
        cornerRadius = CornerRadius(cornerRadius - half),
        style = Stroke(width = strokeWidth, pathEffect = PathEffect.dashPathEffect(DASH_INTERVALS)),
    )
}
