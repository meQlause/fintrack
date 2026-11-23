package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// DOC-08: the badge colour of each supported source app, as the prototype APPS table; unknown apps are grey
object FintrackAppBadges {
    private val colors =
        mapOf(
            "BCA" to Color(color = 0xFF1D4ED8),
            "GoPay" to Color(color = 0xFF047857),
            "DANA" to Color(color = 0xFF0369A1),
        )
    private val fallback: Color = Color(color = 0xFF71717A)

    fun colorOf(source: String): Color = colors[source] ?: fallback

    // the one-letter mark shown in the badge
    fun letterOf(source: String): String = source.take(1).uppercase()
}
