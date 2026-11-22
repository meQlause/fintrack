package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

// DOC-08: maps the Fintrack palette onto Material3 so built-in components follow the brand
internal fun FintrackColors.toColorScheme(isDark: Boolean): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        background = surfaces.background,
        onBackground = texts.onSurface,
        surface = surfaces.surface,
        onSurface = texts.onSurface,
        surfaceVariant = surfaces.surfaceVariant,
        onSurfaceVariant = texts.onSurfaceVariant,
        outline = surfaces.outline,
        outlineVariant = surfaces.outline,
        primary = brands.primary,
        onPrimary = brands.onPrimary,
        primaryContainer = brands.primaryContainer,
        onPrimaryContainer = brands.onPrimaryContainer,
        error = semantics.error,
    )
}
