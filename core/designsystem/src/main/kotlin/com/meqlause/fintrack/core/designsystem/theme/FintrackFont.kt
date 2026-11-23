package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.meqlause.fintrack.core.designsystem.R

// DOC-08: Plus Jakarta Sans is bundled as one variable font (OFL, assets/licenses)
internal object FintrackFont {
    val family: FontFamily =
        FontFamily(
            bundledFont(weight = 400),
            bundledFont(weight = 500),
            bundledFont(weight = 600),
            bundledFont(weight = 700),
            bundledFont(weight = 800),
        )

    private fun bundledFont(weight: Int): Font =
        Font(
            resId = R.font.plus_jakarta_sans,
            weight = FontWeight(weight = weight),
            variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
        )
}
