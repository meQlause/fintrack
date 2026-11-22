package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.meqlause.fintrack.core.designsystem.theme.FintrackMotion
import com.meqlause.fintrack.core.designsystem.theme.toSpec
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private const val SQUISH_LOW = 0.8f
private const val SQUISH_HIGH = 1.14f
private const val SQUISH_SETTLE = 0.97f
private const val SQUISH_FIRST_STEP = 0.18f
private const val SQUISH_PEAK_STEP = 0.52f
private const val SQUISH_SETTLE_STEP = 0.76f
private const val FULL_TURN = 360f
private const val BURST_DONE = 1f

// DOC-09: animated state of the centre add button: squish on press, plus spins, a burst of dots
@Stable
internal class FabFeel {
    val squish = Animatable(initialValue = 1f)
    val spin = Animatable(initialValue = 0f)
    val burst = Animatable(initialValue = BURST_DONE)

    suspend fun play(motion: FintrackMotion) {
        val duration = motion.slow.durationMillis
        coroutineScope {
            launch { squish.animateTo(targetValue = 1f, animationSpec = squishSpec(duration)) }
            launch { spin.animateTo(spin.value + FULL_TURN, motion.slow.toSpec()) }
            launch {
                burst.snapTo(0f)
                burst.animateTo(BURST_DONE, motion.slow.toSpec())
            }
        }
    }

    private fun squishSpec(duration: Int) =
        keyframes<Float> {
            durationMillis = duration
            1f at 0
            SQUISH_LOW at (duration * SQUISH_FIRST_STEP).toInt()
            SQUISH_HIGH at (duration * SQUISH_PEAK_STEP).toInt()
            SQUISH_SETTLE at (duration * SQUISH_SETTLE_STEP).toInt()
            1f at duration
        }
}
