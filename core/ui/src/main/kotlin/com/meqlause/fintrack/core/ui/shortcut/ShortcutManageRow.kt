package com.meqlause.fintrack.core.ui.shortcut

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.RowSlots
import com.meqlause.fintrack.core.designsystem.component.RowStyle
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.icon.IconBox
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.designsystem.icon.TintedIconBox
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.QuickEntryId
import com.meqlause.fintrack.core.ui.R
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-039, DOC-08: one shortcut row; a tap edits it when the host offers a form, the trash button deletes it
@Composable
internal fun ShortcutManageRow(
    params: ShortcutTileParams,
    actions: ShortcutManageActions,
    showDivider: Boolean,
) {
    ScreenRow(
        style =
            RowStyle(
                showDivider = showDivider,
                onClick = actions.onEdit?.let { onEdit -> { onEdit(params.quickEntryId) } },
                contentPadding =
                    PaddingValues(
                        horizontal = FintrackScreenSizes.rowPaddingHorizontal,
                        vertical = FintrackScreenSizes.infoRowVertical,
                    ),
            ),
        slots =
            RowSlots(
                leading = { ShortcutRowIcon(params = params) },
                trailing = { ShortcutRowTrailing(params = params, onDelete = actions.onDelete) },
            ),
    ) {
        ShortcutRowLabels(params = params)
    }
}

// FR-039: the shortcut name over its wallet
@Composable
private fun ShortcutRowLabels(params: ShortcutTileParams) {
    Text(
        text = params.title,
        style = FintrackScreenText.style(size = 14f, weight = 600),
        color = FintrackTheme.colors.texts.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    Text(
        text = params.walletName,
        style = FintrackScreenText.rowSub(),
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

// FR-039: the category icon in its own colour, as the option rows of the pickers
@Composable
private fun ShortcutRowIcon(params: ShortcutTileParams) {
    TintedIconBox(
        look = IconLook(iconKey = params.appearance.iconKey, colorKey = params.appearance.colorKey),
        tile =
            IconBox(
                size = FintrackScreenSizes.smallIconBox + FintrackTheme.spacing.xxSmall,
                glyph = FintrackScreenSizes.circleGlyph,
                shape = RoundedCornerShape(FintrackScreenSizes.smallIconCorner),
            ),
    )
}

// FR-039: the amount and the delete button at the end of a row
@Composable
private fun ShortcutRowTrailing(
    params: ShortcutTileParams,
    onDelete: (QuickEntryId) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = MoneyFormatter.format(params.amount),
            style = FintrackScreenText.number(size = 14f, weight = 700),
            color = FintrackTheme.colors.texts.onSurface,
        )
        Icon(
            painter = painterResource(DesignR.drawable.ic_trash),
            contentDescription = stringResource(R.string.shortcut_delete_description),
            tint = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier =
                Modifier
                    .padding(start = FintrackTheme.spacing.small)
                    .clip(FintrackTheme.shapes.full)
                    .clickable(role = Role.Button) { onDelete(params.quickEntryId) }
                    .padding(FintrackTheme.spacing.xSmall)
                    .size(FintrackTheme.sizes.iconSmall),
        )
    }
}
