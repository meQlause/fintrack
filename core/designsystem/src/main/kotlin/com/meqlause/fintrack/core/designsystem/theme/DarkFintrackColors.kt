package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// DOC-08: dark column of the colour table (design/project/Main.dc.html, .app.dark)
internal val darkFintrackColors =
    FintrackColors(
        surfaces =
            SurfaceColors(
                background = Color(color = 0xFF0E1013),
                surface = Color(color = 0xFF16191E),
                surfaceVariant = Color(color = 0xFF1E2228),
                outline = Color(color = 0xFF262A31),
            ),
        texts =
            TextColors(
                onSurface = Color(color = 0xFFECEDEF),
                onSurfaceVariant = Color(color = 0xFF9BA1AA),
                faint = Color(color = 0xFF6B7280),
            ),
        brands =
            BrandColors(
                primary = Color(color = 0xFF34D1BF),
                onPrimary = Color(color = 0xFF062A26),
                primaryContainer = Color(color = 0xFF11332F),
                onPrimaryContainer = Color(color = 0xFFB9F3EA),
            ),
        semantics =
            SemanticColors(
                income = Color(color = 0xFF4ADE80),
                expense = Color(color = 0xFFFB7A95),
                transfer = Color(color = 0xFF7AB3FF),
                warning = Color(color = 0xFFFBBF24),
                error = Color(color = 0xFFFB923C),
            ),
    )
