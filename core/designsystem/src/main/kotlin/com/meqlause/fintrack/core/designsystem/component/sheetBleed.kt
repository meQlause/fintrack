package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp

// DOC-08: the prototype's negative horizontal margin; a scrolling row runs to the screen edge inside a padded sheet
fun Modifier.sheetBleed(amount: Dp): Modifier =
    layout { measurable, constraints ->
        val extra = amount.roundToPx() * 2
        val width = constraints.maxWidth + extra
        val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
        layout(constraints.maxWidth, placeable.height) { placeable.place(-extra / 2, 0) }
    }
