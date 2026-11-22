package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

private const val TAB_COUNT = 4
private const val SLOT_COUNT = 5
private const val CENTRE_OF_SLOT = 0.5f
private const val TABS_BEFORE_FAB = 2

// DOC-09: floating liquid bottom bar: two tabs, the centre add button, two tabs; stateless, selection is hoisted
@Composable
fun FintrackBottomBar(
    items: List<FintrackBarItem>,
    selectedIndex: Int,
    callbacks: FintrackBottomBarCallbacks,
    modifier: Modifier = Modifier,
) {
    require(items.size == TAB_COUNT) { "DOC-09: the bottom bar has exactly $TAB_COUNT tabs" }
    val sizes = FintrackTheme.sizes
    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxWidth()
                .height(sizes.navBarHeight)
                .padding(start = sizes.navBarInset, end = sizes.navBarInset, bottom = sizes.navBarBottom),
    ) {
        val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val targetX = widthPx / SLOT_COUNT * (slotOf(selectedIndex) + CENTRE_OF_SLOT)
        val centreX by animateFloatAsState(targetValue = targetX, animationSpec = FintrackTheme.motion.bubble)
        BarCapsule(centreX = { centreX })
        BarRipple(selectedIndex = selectedIndex, centreX = { centreX })
        BarBubble(centreX = { centreX })
        Row(modifier = Modifier.fillMaxSize()) {
            TabPair(items = items.subList(0, TABS_BEFORE_FAB), offset = 0, selectedIndex, callbacks)
            FabSlot(onClick = callbacks.onAddClick)
            TabPair(
                items = items.subList(TABS_BEFORE_FAB, TAB_COUNT),
                offset = TABS_BEFORE_FAB,
                selectedIndex,
                callbacks,
            )
        }
    }
}

// DOC-09: slot 2 of 5 is the add button; tabs 0-1 sit left of it and tabs 2-3 right of it
private fun slotOf(index: Int): Float = if (index < TABS_BEFORE_FAB) index.toFloat() else (index + 1).toFloat()

@Composable
private fun RowScope.TabPair(
    items: List<FintrackBarItem>,
    offset: Int,
    selectedIndex: Int,
    callbacks: FintrackBottomBarCallbacks,
) {
    items.forEachIndexed { position, item ->
        val index = offset + position
        BarTabButton(
            item = item,
            selected = index == selectedIndex,
            onClick = { callbacks.onSelect(index) },
        )
    }
}

@Composable
private fun RowScope.FabSlot(onClick: () -> Unit) {
    Box(
        modifier = Modifier.weight(1f).fillMaxHeight(),
        contentAlignment = Alignment.TopCenter,
    ) {
        FintrackFab(
            onClick = onClick,
            modifier = Modifier.offset(y = -FintrackTheme.sizes.navBarFabLift),
        )
    }
}
