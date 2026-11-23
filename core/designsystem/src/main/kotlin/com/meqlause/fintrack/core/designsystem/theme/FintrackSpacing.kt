package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

// DOC-08: 4 dp grid (08 names xxs..xxxl, renamed to 4+ characters per 05 section 3); screen gutter is large
@Immutable
class FintrackSpacing {
    val xxSmall: Dp = Dp(value = 2f)
    val xSmall: Dp = Dp(value = 4f)
    val small: Dp = Dp(value = 8f)
    val medium: Dp = Dp(value = 12f)
    val large: Dp = Dp(value = 16f)
    val xLarge: Dp = Dp(value = 24f)
    val xxLarge: Dp = Dp(value = 32f)
    val xxxLarge: Dp = Dp(value = 48f)

    // DOC-08: the mockup screen gutter is 20 px
    val gutter: Dp = Dp(value = 20f)
    val section: Dp = Dp(value = 18f)
    val stack: Dp = Dp(value = 14f)
    val chipInset: Dp = Dp(value = 7f)
    val block: Dp = Dp(value = 22f)
    val hero: Dp = Dp(value = 26f)
}
