package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

// DOC-08: corner radii; full is the 50% circle for FAB, avatars and keypad keys
@Immutable
class FintrackShapes {
    val small: CornerBasedShape = RoundedCornerShape(size = Dp(value = 8f))
    val medium: CornerBasedShape = RoundedCornerShape(size = Dp(value = 16f))
    val large: CornerBasedShape = RoundedCornerShape(size = Dp(value = 24f))
    val sheet: Shape = RoundedCornerShape(topStart = Dp(value = 24f), topEnd = Dp(value = 24f))
    val full: Shape = CircleShape

    // DOC-09: category icon box corner from the prototype (12 px), not a circle
    val categoryIcon: CornerBasedShape = RoundedCornerShape(size = Dp(value = 12f))
}
