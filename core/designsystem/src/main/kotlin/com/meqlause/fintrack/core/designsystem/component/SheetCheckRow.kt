package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: the prototype .cbox row, a 20 px rounded checkbox with a 13 px bold label
@Composable
fun SheetCheckRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val shape = FintrackTheme.shapes.chip
    Row(
        modifier = modifier.toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        Box(
            modifier =
                Modifier
                    .size(FintrackScreenSizes.checkBox)
                    .clip(shape)
                    .background(if (checked) colors.brands.primary else colors.surfaces.background)
                    .border(
                        FintrackScreenSizes.checkBoxBorder,
                        if (checked) colors.brands.primary else colors.surfaces.outline,
                        shape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = colors.brands.onPrimary,
                    modifier = Modifier.size(FintrackScreenSizes.checkGlyph),
                )
            }
        }
        Text(
            text = label,
            style = FintrackScreenText.style(size = 13f, weight = 600),
            color = if (checked) colors.brands.primary else colors.texts.onSurfaceVariant,
        )
    }
}
