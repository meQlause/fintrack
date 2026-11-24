package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08, DOC-09: draws the open sheets over the screen and the bottom bar, dimmed, each anchored to the bottom
@Composable
fun SheetLayer(
    portal: SheetPortal,
    modifier: Modifier = Modifier,
) {
    portal.entries.forEach { entry ->
        val scrim =
            FintrackTheme.colors.texts.onSurface
                .copy(alpha = FintrackTheme.alphas.scrim)
        Box(
            modifier = modifier.fillMaxSize().background(scrim).tapOnly(entry.dismiss),
            contentAlignment = Alignment.BottomCenter,
        ) {
            SheetPanel(modifier = entry.modifier.navigationBarsPadding().tapOnly {}, content = entry.content)
        }
    }
}

// DOC-08: a tap with no ripple; on the panel it keeps taps from reaching the scrim behind it
@Composable
private fun Modifier.tapOnly(onClick: () -> Unit): Modifier =
    clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
