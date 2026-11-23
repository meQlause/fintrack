package com.meqlause.fintrack.feature.settings

import androidx.annotation.StringRes

// DOC-07: the OEM name and its numbered steps, both as string resources so the copy stays Indonesian
internal data class BatteryGuide(
    @param:StringRes val brand: Int,
    @param:StringRes val steps: List<Int>,
)
