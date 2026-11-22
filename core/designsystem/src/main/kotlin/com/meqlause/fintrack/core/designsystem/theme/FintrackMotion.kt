package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable
import kotlin.math.roundToInt

private const val FULL_SPEED = 1f

// DOC-08: quick, medium and slow motion tokens; durations scale with the system animator setting
@Immutable
class FintrackMotion(
    private val durationScale: Float = FULL_SPEED,
) {
    // DOC-08: standard and emphasized curves follow Material motion; emphasized decelerate is the M3 curve
    private val standard: Easing = CubicBezierEasing(a = 0.4f, b = 0f, c = 0.2f, d = 1f)
    private val emphasizedDecelerate: Easing = CubicBezierEasing(a = 0.05f, b = 0.7f, c = 0.1f, d = 1f)
    private val emphasized: Easing = CubicBezierEasing(a = 0.2f, b = 0f, c = 0f, d = 1f)

    val quick: MotionToken = motionToken(millis = 120, easing = standard)
    val medium: MotionToken = motionToken(millis = 250, easing = emphasizedDecelerate)
    val slow: MotionToken = motionToken(millis = 400, easing = emphasized)

    private fun motionToken(
        millis: Int,
        easing: Easing,
    ): MotionToken = MotionToken(durationMillis = (millis * durationScale).roundToInt(), easing = easing)
}
