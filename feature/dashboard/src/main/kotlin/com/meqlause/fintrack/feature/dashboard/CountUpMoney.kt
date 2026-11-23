package com.meqlause.fintrack.feature.dashboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import kotlin.math.roundToLong

// FR-040, DOC-08: the hero balance counts up from zero once when Beranda appears; the final value is exact
@Composable
internal fun CountUpMoney(
    money: Money,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(0f) }
    val duration = FintrackTheme.motion.slow.durationMillis
    val easing = FintrackTheme.motion.slow.easing
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec =
                tween(
                    durationMillis = duration,
                    easing = easing,
                ),
        )
    }
    val shown = Money((money.rupiah.toDouble() * progress.value).roundToLong())
    HeroAmount(formatted = MoneyFormatter.format(shown), modifier = modifier)
}
