package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// DOC-08: light column of the colour table (design/project/Main.dc.html, .app.light)
internal val lightFintrackColors =
    FintrackColors(
        surfaces =
            SurfaceColors(
                background = Color(color = 0xFFF6F6F3),
                surface = Color(color = 0xFFFFFFFF),
                surfaceVariant = Color(color = 0xFFF1F1EE),
                outline = Color(color = 0xFFE8E8E4),
            ),
        texts =
            TextColors(
                onSurface = Color(color = 0xFF18181B),
                onSurfaceVariant = Color(color = 0xFF71717A),
                faint = Color(color = 0xFFA1A1AA),
            ),
        brands =
            BrandColors(
                primary = Color(color = 0xFF0B7F74),
                onPrimary = Color(color = 0xFFFFFFFF),
                primaryContainer = Color(color = 0xFFE3F4F1),
                onPrimaryContainer = Color(color = 0xFF0B5F57),
            ),
        semantics =
            SemanticColors(
                income = Color(color = 0xFF15803D),
                expense = Color(color = 0xFFD6325A),
                transfer = Color(color = 0xFF2563EB),
                warning = Color(color = 0xFFB45309),
                error = Color(color = 0xFFC2410C),
            ),
    )
