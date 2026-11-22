package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import kotlin.math.roundToInt

// DOC-09: the primary bubble that sits in the notch and slides to the selected tab on a spring
@Composable
internal fun BarBubble(centreX: () -> Float) {
    val sizes = FintrackTheme.sizes
    Box(
        modifier =
            Modifier
                .size(sizes.navBarBubble)
                .offset {
                    // DOC-09: centred on the capsule's top edge, half above it, half in the notch
                    val half = sizes.navBarBubble.toPx() / 2f
                    IntOffset((centreX() - half).roundToInt(), (sizes.navBarCapsuleTop.toPx() - half).roundToInt())
                }.clip(CircleShape)
                .background(FintrackTheme.colors.brands.primary),
    )
}
