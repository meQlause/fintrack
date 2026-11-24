package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable

// DOC-08: one motion step, a duration and the easing curve that shapes it
@Immutable
data class MotionToken(
    val durationMillis: Int,
    val easing: Easing,
)

// DOC-08: components animate with the token, so the system "remove animations" setting shortens them too
internal fun <T> MotionToken.toSpec(): FiniteAnimationSpec<T> = tween(durationMillis = durationMillis, easing = easing)
