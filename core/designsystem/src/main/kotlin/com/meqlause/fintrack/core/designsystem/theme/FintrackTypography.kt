package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.meqlause.fintrack.core.designsystem.R

private const val TABULAR_NUMBERS = "tnum"

// DOC-08: typography table; Plus Jakarta Sans is bundled as one variable font (OFL, assets/licenses)
@Immutable
class FintrackTypography {
    private val family =
        FontFamily(
            bundledFont(weight = 400),
            bundledFont(weight = 500),
            bundledFont(weight = 600),
            bundledFont(weight = 700),
            bundledFont(weight = 800),
        )

    val numberHero: TextStyle = typeStyle(size = 40f, lineHeight = 48f, weight = 800, tabular = true)

    // DOC-08: mockup hero balance digits (44 px / 700) and the muted Rp prefix (20 px / 600)
    val numberDisplay: TextStyle = typeStyle(size = 44f, lineHeight = 46f, weight = 700, tabular = true)
    val prefixDisplay: TextStyle = typeStyle(size = 20f, lineHeight = 24f, weight = 600)

    // DOC-08: mockup .lbl (12 px / 600) and the 12.5 px pill and tile title
    val caption: TextStyle = typeStyle(size = 12f, lineHeight = 16f, weight = 600)
    val captionWide: TextStyle = typeStyle(size = 12.5f, lineHeight = 16f, weight = 600)
    val numberCompact: TextStyle = typeStyle(size = 13.5f, lineHeight = 18f, weight = 700, tabular = true)
    val badge: TextStyle = typeStyle(size = 10.5f, lineHeight = 14f, weight = 700)
    val tab: TextStyle = typeStyle(size = 13f, lineHeight = 18f, weight = 600)
    val tabWide: TextStyle = typeStyle(size = 13.5f, lineHeight = 18f, weight = 600)
    val micro: TextStyle = typeStyle(size = 11f, lineHeight = 14f, weight = 600, tabular = true)
    val numberLarge: TextStyle = typeStyle(size = 28f, lineHeight = 36f, weight = 700, tabular = true)
    val numberTotal: TextStyle = typeStyle(size = 20f, lineHeight = 26f, weight = 700, tabular = true)
    val sheetTitle: TextStyle = typeStyle(size = 17f, lineHeight = 24f, weight = 700)
    val numberDetail: TextStyle = typeStyle(size = 30f, lineHeight = 38f, weight = 700, tabular = true)
    val numberMedium: TextStyle = typeStyle(size = 18f, lineHeight = 24f, weight = 700, tabular = true)

    // DOC-09: prototype wallet chip balance, 15 px bold
    val numberSmall: TextStyle = typeStyle(size = 15f, lineHeight = 20f, weight = 700, tabular = true)

    // DOC-09: the prototype .row amount, 14 px semibold with tabular figures
    val numberRow: TextStyle = typeStyle(size = 14f, lineHeight = 20f, weight = 600, tabular = true)
    val headlineSmall: TextStyle = typeStyle(size = 24f, lineHeight = 32f, weight = 700)

    // DOC-09: 20 sp bold title of every tab header (prototype: 20px / 700)
    val tabTitle: TextStyle = typeStyle(size = 20f, lineHeight = 28f, weight = 700)
    val titleMedium: TextStyle = typeStyle(size = 16f, lineHeight = 24f, weight = 600)
    val bodyLarge: TextStyle = typeStyle(size = 16f, lineHeight = 24f, weight = 400)
    val bodyMedium: TextStyle = typeStyle(size = 14f, lineHeight = 20f, weight = 400)
    val labelLarge: TextStyle = typeStyle(size = 14f, lineHeight = 20f, weight = 600)
    val labelMedium: TextStyle = typeStyle(size = 12f, lineHeight = 16f, weight = 500)

    private fun typeStyle(
        size: Float,
        lineHeight: Float,
        weight: Int,
        tabular: Boolean = false,
    ): TextStyle =
        TextStyle(
            fontFamily = family,
            fontWeight = FontWeight(weight = weight),
            fontSize = TextUnit(value = size, type = TextUnitType.Sp),
            lineHeight = TextUnit(value = lineHeight, type = TextUnitType.Sp),
            fontFeatureSettings = if (tabular) TABULAR_NUMBERS else null,
        )

    private fun bundledFont(weight: Int): Font =
        Font(
            resId = R.font.plus_jakarta_sans,
            weight = FontWeight(weight = weight),
            variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
        )
}
