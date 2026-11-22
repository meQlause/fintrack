package com.meqlause.fintrack.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

// DOC-08: component sizes that are not spacing steps; components never hold a raw dp
@Immutable
class FintrackSizes {
    val hairline: Dp = Dp(value = 1f)
    val touchTarget: Dp = Dp(value = 48f)
    val iconSmall: Dp = Dp(value = 18f)
    val iconMedium: Dp = Dp(value = 22f)
    val iconLarge: Dp = Dp(value = 40f)
    val iconTile: Dp = Dp(value = 32f)
    val chipHeight: Dp = Dp(value = 34f)
    val formRowHeight: Dp = Dp(value = 52f)
    val segmentHeight: Dp = Dp(value = 40f)
    val switchWidth: Dp = Dp(value = 44f)
    val switchHeight: Dp = Dp(value = 26f)
    val knobSize: Dp = Dp(value = 20f)
    val knobInset: Dp = Dp(value = 3f)
    val dragHandleWidth: Dp = Dp(value = 36f)
    val dragHandleHeight: Dp = Dp(value = 4f)
    val snackbarHeight: Dp = Dp(value = 44f)
    val drawerMaxHeight: Dp = Dp(value = 640f)
}
