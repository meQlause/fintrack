package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.runtime.staticCompositionLocalOf

/** DOC-09: the cover the sheets report to; the app shell provides the one its bar listens to. */
val LocalSheetCover = staticCompositionLocalOf { SheetCover() }
