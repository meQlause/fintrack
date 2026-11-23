package com.meqlause.fintrack.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// DOC-08: a rounded square of any size tinted with a palette colour, as the prototype hero icons (52 px, 16 px radius)
@Composable
fun TintedIconBox(
    look: IconLook,
    tile: IconBox,
    modifier: Modifier = Modifier,
) {
    IconCircle(look = look, modifier = modifier, tile = tile)
}
