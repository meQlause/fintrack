package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.em

// DOC-08: prototype font sizes that the shared typography table has no entry for (px = sp)
object FintrackScreenText {
    fun label(): TextStyle = style(size = 12f, weight = 600, spacing = 0.02f)

    fun netWorth(): TextStyle = number(size = 30f, weight = 700)

    fun total(): TextStyle = number(size = 17f, weight = 700)

    fun legend(weight: Int): TextStyle = style(size = 12.5f, weight = weight)

    fun legendNumber(): TextStyle = number(size = 12.5f, weight = 600)

    fun rowTitle(): TextStyle = style(size = 14.5f, weight = 600)

    fun rowAmount(): TextStyle = number(size = 14.5f, weight = 600)

    fun rowSub(weight: Int = 400): TextStyle = style(size = 12f, weight = weight)

    fun rowSubNumber(): TextStyle = number(size = 12f, weight = 400)

    fun button(): TextStyle = style(size = 14f, weight = 600)

    // prototype .num: tabular figures and -0.02em tracking
    fun number(
        size: Float,
        weight: Int,
    ): TextStyle = style(size = size, weight = weight, spacing = -0.02f).copy(fontFeatureSettings = "tnum")

    fun style(
        size: Float,
        weight: Int,
        spacing: Float = 0f,
    ): TextStyle =
        TextStyle(
            fontFamily = FintrackFont.family,
            fontSize = TextUnit(value = size, type = TextUnitType.Sp),
            lineHeight = TextUnit(value = size * 1.4f, type = TextUnitType.Sp),
            fontWeight = FontWeight(weight = weight),
            letterSpacing = spacing.em,
        )
}
