package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.runtime.staticCompositionLocalOf

/** DOC-08: the portal that draws sheets over the whole screen; null draws a sheet where it is declared. */
val LocalSheetPortal = staticCompositionLocalOf<SheetPortal?> { null }
