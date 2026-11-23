package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp

// DOC-08: the prototype's negative horizontal margin; a scrolling row runs to the screen edge inside a padded sheet
data class SheetBleed(
    val bleed: Dp,
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val extra = bleed.roundToPx() * 2
        val width = constraints.maxWidth + extra
        val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
        return layout(constraints.maxWidth, placeable.height) { placeable.place(-extra / 2, 0) }
    }
}
