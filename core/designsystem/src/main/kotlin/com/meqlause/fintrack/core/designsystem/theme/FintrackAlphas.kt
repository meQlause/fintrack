package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable

// DOC-08: alpha tokens; category icon boxes take their colour at 10% on light, 16% on dark (prototype tint())
@Immutable
class FintrackAlphas {
    val categoryTintLight: Float = tokenAlpha(value = 0.10f)
    val categoryTintDark: Float = tokenAlpha(value = 0.16f)

    // names a literal alpha so that the token table keeps its numbers as named arguments (05 section 5)
    private fun tokenAlpha(value: Float): Float = value
}
