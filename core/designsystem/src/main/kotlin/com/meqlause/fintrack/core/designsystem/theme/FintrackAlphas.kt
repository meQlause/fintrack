package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable

// DOC-08: alpha tokens; category icons sit on a 16%-alpha circle of their colour
@Immutable
class FintrackAlphas {
    val categoryTint: Float = tokenAlpha(value = 0.16f)

    // names a literal alpha so that the token table keeps its numbers as named arguments (05 section 5)
    private fun tokenAlpha(value: Float): Float = value
}
