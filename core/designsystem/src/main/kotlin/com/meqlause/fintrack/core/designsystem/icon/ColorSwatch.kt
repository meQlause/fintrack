package com.meqlause.fintrack.core.designsystem.icon

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.CategoryPalette
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: a choosable category colour, a solid disc with a ring when it is the chosen one
@Composable
fun ColorSwatch(
    colorKey: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val surface = FintrackTheme.colors.surfaces.background
    val ring = FintrackTheme.colors.texts.onSurface
    Box(
        modifier =
            modifier
                .size(FintrackTheme.sizes.categoryIcon)
                .background(CategoryPalette.colorFor(colorKey = colorKey, surface = surface), CircleShape)
                .border(
                    width = FintrackTheme.sizes.selectionRing,
                    color = if (selected) ring else ring.copy(alpha = 0f),
                    shape = CircleShape,
                ).selectable(selected = selected, role = Role.RadioButton, onClick = onSelect),
    )
}
