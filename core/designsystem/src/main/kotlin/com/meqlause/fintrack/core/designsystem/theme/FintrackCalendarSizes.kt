package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.ui.unit.Dp

// DOC-08: prototype metrics of the period picker, its month tiles and its day grid
object FintrackCalendarSizes {
    val monthTile: Dp = Dp(value = 50f)
    val dayCell: Dp = Dp(value = 44f)
    val ringWidth: Dp = Dp(value = 1.5f)
    val dotGap: Dp = Dp(value = 2f)
    val chevronTarget: Dp = Dp(value = 36f)
    val disabledAlpha: Float = tokenAlpha(value = 0.35f)

    // names a literal alpha so that the token table keeps its numbers as named arguments (05 section 5)
    private fun tokenAlpha(value: Float): Float = value
}
