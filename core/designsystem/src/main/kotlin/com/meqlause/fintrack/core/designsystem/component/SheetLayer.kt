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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08, DOC-09: draws the open sheets over the screen and the bottom bar, dimmed, each anchored to the bottom;
// only the newest sheet is seen, the ones under it stay composed (keeping their state) but are hidden, so a
// picker opened over a form never shows a second drag handle or a second scrim
@Composable
fun SheetLayer(
    portal: SheetPortal,
    modifier: Modifier = Modifier,
) {
    val entries = portal.entries
    entries.forEachIndexed { index, entry ->
        if (index == entries.lastIndex) {
            TopSheet(entry = entry, modifier = modifier)
        } else {
            HiddenSheet(entry = entry, modifier = modifier)
        }
    }
}

@Composable
private fun TopSheet(
    entry: SheetEntry,
    modifier: Modifier,
) {
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

// DOC-08: a sheet covered by a newer one; not drawn and not reachable, but still in the composition
@Composable
private fun HiddenSheet(
    entry: SheetEntry,
    modifier: Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize().alpha(0f).clearAndSetSemantics {},
        contentAlignment = Alignment.BottomCenter,
    ) {
        SheetPanel(modifier = entry.modifier.navigationBarsPadding(), content = entry.content)
    }
}

// DOC-08: a tap with no ripple; on the panel it keeps taps from reaching the scrim behind it
@Composable
private fun Modifier.tapOnly(onClick: () -> Unit): Modifier =
    clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
