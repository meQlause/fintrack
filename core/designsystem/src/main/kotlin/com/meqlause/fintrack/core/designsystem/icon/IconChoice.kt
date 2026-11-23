package com.meqlause.fintrack.core.designsystem.icon

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: a choosable category icon; the chosen one gets a ring in its colour
@Composable
fun IconChoice(
    iconKey: String,
    colorKey: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ring = FintrackTheme.colors.texts.onSurface
    Box(
        modifier =
            modifier
                .clip(FintrackTheme.shapes.categoryIcon)
                .selectable(selected = selected, role = Role.RadioButton, onClick = onSelect),
    ) {
        IconCircle(
            iconKey = iconKey,
            colorKey = colorKey,
            modifier =
                Modifier.border(
                    width = FintrackTheme.sizes.selectionRing,
                    color = if (selected) ring else ring.copy(alpha = 0f),
                    shape = FintrackTheme.shapes.categoryIcon,
                ),
        )
    }
}
