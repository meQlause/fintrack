package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable

// DOC-08: one motion step, a duration and the easing curve that shapes it
@Immutable
data class MotionToken(
    val durationMillis: Int,
    val easing: Easing,
)
