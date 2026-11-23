package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// DOC-08: category colour keys with their light and dark values (prototype this.COLORS, first = light, second = dark)
internal object CategoryPalette {
    private val light: Map<String, Color> =
        mapOf(
            "orange" to Color(color = 0xFFE8833A),
            "blue" to Color(color = 0xFF3B7BEA),
            "pink" to Color(color = 0xFFDD5C9B),
            "purple" to Color(color = 0xFF9466DB),
            "violet" to Color(color = 0xFF6E66DE),
            "red" to Color(color = 0xFFDE5656),
            "cyan" to Color(color = 0xFF1FA2BF),
            "rose" to Color(color = 0xFFE0567A),
            "green" to Color(color = 0xFF2E9F62),
            "teal" to Color(color = 0xFF14A394),
            "gray" to Color(color = 0xFF9CA3AF),
            "indigo" to Color(color = 0xFF5A6BDB),
            "amber" to Color(color = 0xFFD99A2B),
            "slate" to Color(color = 0xFF475569),
        )

    private val dark: Map<String, Color> =
        mapOf(
            "orange" to Color(color = 0xFFF5A66B),
            "blue" to Color(color = 0xFF7FAAF5),
            "pink" to Color(color = 0xFFF08DBE),
            "purple" to Color(color = 0xFFB99BEC),
            "violet" to Color(color = 0xFFA19CF0),
            "red" to Color(color = 0xFFF08A8A),
            "cyan" to Color(color = 0xFF62C9DE),
            "rose" to Color(color = 0xFFF28AA5),
            "green" to Color(color = 0xFF6CCB95),
            "teal" to Color(color = 0xFF5ED3C5),
            "gray" to Color(color = 0xFF8B929C),
            "indigo" to Color(color = 0xFF919DEB),
            "amber" to Color(color = 0xFFF0BE63),
            "slate" to Color(color = 0xFFA3B1C2),
        )

    private val fallback: Color = Color(color = 0xFF9CA3AF)
    private const val DARK_LUMINANCE = 0.5f

    // the surface decides the column: a dark surface reads the second value; unknown keys fall back to gray
    fun isDarkSurface(surface: Color): Boolean = surface.luminance() < DARK_LUMINANCE

    fun colorFor(
        colorKey: String,
        surface: Color,
    ): Color {
        val table = if (isDarkSurface(surface)) dark else light
        return table[colorKey] ?: fallback
    }
}
