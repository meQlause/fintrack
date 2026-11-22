package com.meqlause.fintrack.core.designsystem

import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.theme.darkFintrackColors
import com.meqlause.fintrack.core.designsystem.theme.lightFintrackColors
import org.junit.Assert.assertEquals
import org.junit.Test

class FintrackColorsTest {
    // DOC-08: colour table, light column
    @Test
    fun `given light palette when read then primary and background match the colour table (DOC-08)`() {
        assertEquals(Color(color = 0xFF0B7F74), lightFintrackColors.brands.primary)
        assertEquals(Color(color = 0xFFF6F6F3), lightFintrackColors.surfaces.background)
    }

    // DOC-08: colour table, dark column
    @Test
    fun `given dark palette when read then primary and background match the colour table (DOC-08)`() {
        assertEquals(Color(color = 0xFF34D1BF), darkFintrackColors.brands.primary)
        assertEquals(Color(color = 0xFF0E1013), darkFintrackColors.surfaces.background)
    }
}
