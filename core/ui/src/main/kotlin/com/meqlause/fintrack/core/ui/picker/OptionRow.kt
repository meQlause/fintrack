package com.meqlause.fintrack.core.ui.picker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
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

// DOC-08: the standard choice row, an optional tinted tile, a name over a grey hint and a radio mark at the end
@Composable
internal fun OptionRow(
    option: PickerOption,
    isSelected: Boolean,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    ScreenRow(
        modifier =
            Modifier.semantics {
                role = Role.RadioButton
                selected = isSelected
            },
        style =
            RowStyle(
                showDivider = showDivider,
                onClick = onClick,
                contentPadding =
                    PaddingValues(
                        horizontal = FintrackScreenSizes.rowPaddingHorizontal,
                        vertical = FintrackScreenSizes.infoRowVertical,
                    ),
            ),
        slots = RowSlots(leading = { option.look?.let { OptionTile(it) } }, trailing = { OptionRadio(isSelected) }),
    ) {
        OptionLabels(option = option)
    }
}

// DOC-08: the name of an option over its grey hint
@Composable
private fun OptionLabels(option: PickerOption) {
    Text(
        text = option.label,
        style = FintrackScreenText.style(size = 14f, weight = 600),
        color = FintrackTheme.colors.texts.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    option.hint?.let { hint ->
        Text(
            text = hint,
            style = FintrackScreenText.rowSub(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

@Composable
private fun OptionTile(look: IconLook) {
    TintedIconBox(
        look = look,
        tile =
            IconBox(
                size = FintrackScreenSizes.smallIconBox + FintrackTheme.spacing.xxSmall,
                glyph = FintrackScreenSizes.circleGlyph,
                shape = RoundedCornerShape(FintrackScreenSizes.smallIconCorner),
            ),
    )
}
