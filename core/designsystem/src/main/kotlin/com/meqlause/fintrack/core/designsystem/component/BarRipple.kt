package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.toSpec
import kotlin.math.roundToInt

private const val RESTING = 1f
private const val RING_START_SCALE = 0.9f
private const val RING_GROWTH = 0.85f
private const val RING_ALPHA = 0.5f

// DOC-09: on a tab change a ring spreads from the bubble and a droplet rises from the capsule edge
@Composable
internal fun BarRipple(
    selectedIndex: Int,
    centreX: () -> Float,
) {
    val progress = remember { Animatable(RESTING) }
    val lastIndex = remember { mutableIntStateOf(selectedIndex) }
    val motion = FintrackTheme.motion
    LaunchedEffect(selectedIndex) {
        if (lastIndex.intValue != selectedIndex) {
            lastIndex.intValue = selectedIndex
            progress.snapTo(0f)
            progress.animateTo(RESTING, motion.slow.toSpec())
        }
    }
    BarRing(progress = { progress.value }, centreX = centreX)
    BarDroplet(progress = { progress.value }, centreX = centreX)
}

@Composable
private fun BarRing(
    progress: () -> Float,
    centreX: () -> Float,
) {
    val sizes = FintrackTheme.sizes
    val primary = FintrackTheme.colors.brands.primary
    Box(
        modifier =
            Modifier
                .size(sizes.navBarBubble)
                .offset {
                    val half = sizes.navBarBubble.toPx() / 2f
                    IntOffset((centreX() - half).roundToInt(), (sizes.navBarCapsuleTop.toPx() - half).roundToInt())
                }.graphicsLayer {
                    val scale = RING_START_SCALE + RING_GROWTH * progress()
                    scaleX = scale
                    scaleY = scale
                    alpha = RING_ALPHA * (RESTING - progress())
                }.border(width = sizes.navBarRing, color = primary, shape = CircleShape),
    )
}

@Composable
private fun BarDroplet(
    progress: () -> Float,
    centreX: () -> Float,
) {
    val sizes = FintrackTheme.sizes
    val primary = FintrackTheme.colors.brands.primary
    Box(
        modifier =
            Modifier
                .size(sizes.navBarDrop)
                .offset {
                    val half = sizes.navBarDrop.toPx() / 2f
                    val rise = sizes.navBarTravel.toPx() * progress()
                    IntOffset(
                        (centreX() - half).roundToInt(),
                        (sizes.navBarCapsuleTop.toPx() - half - rise).roundToInt(),
                    )
                }.graphicsLayer { alpha = RESTING - progress() }
                .clip(CircleShape)
                .background(primary),
    )
}
