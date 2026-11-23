package com.meqlause.fintrack.feature.lock

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

private val SHAKE_STEPS = listOf(-12f, 12f, -8f, 8f, 0f)
private const val SHAKE_STEP_MILLIS = 40

// S-02: a wrong PIN shakes the dots once and gives a haptic tap; the returned offset is in dp
@Composable
internal fun rememberPinShake(shakeCount: Int): Animatable<Float, AnimationVector1D> {
    val offset = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(shakeCount) {
        if (shakeCount == 0) return@LaunchedEffect
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        SHAKE_STEPS.forEach { offset.animateTo(it, tween(durationMillis = SHAKE_STEP_MILLIS)) }
    }
    return offset
}
