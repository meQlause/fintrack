package com.meqlause.fintrack.core.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08, DOC-09: a column whose rows fade and shrink away when they leave the list and grow in when they return
@Composable
fun <T : Any> AnimatedRows(
    items: List<T>,
    keyOf: (T) -> Any,
    modifier: Modifier = Modifier,
    rowContent: @Composable (T) -> Unit,
) {
    var kept by remember { mutableStateOf(items) }
    var settled by remember { mutableStateOf(false) }
    LaunchedEffect(items) {
        kept = withLeavers(items = items, kept = kept, keyOf = keyOf)
        settled = true
    }
    val live = items.map(keyOf).toSet()
    Column(modifier = modifier.fillMaxWidth()) {
        withLeavers(items = items, kept = kept, keyOf = keyOf).forEach { item ->
            val itemKey = keyOf(item)
            key(itemKey) {
                AnimatedRow(
                    visible = itemKey in live,
                    appear = settled,
                    onGone = { kept = kept.filterNot { keyOf(it) == itemKey } },
                ) { rowContent(item) }
            }
        }
    }
}

// DOC-08: the rows to draw, the live ones in order with each row that just left kept at its old place
private fun <T : Any> withLeavers(
    items: List<T>,
    kept: List<T>,
    keyOf: (T) -> Any,
): List<T> {
    val live = items.map(keyOf).toSet()
    val merged = items.toMutableList()
    kept.forEachIndexed { index, old ->
        if (keyOf(old) !in live) merged.add(index.coerceAtMost(merged.size), old)
    }
    return merged
}

// DOC-08: rows present on the first frame are shown at once; later ones grow in, and a leaver reports when it is gone
@Composable
private fun AnimatedRow(
    visible: Boolean,
    appear: Boolean,
    onGone: () -> Unit,
    content: @Composable () -> Unit,
) {
    val state = remember { MutableTransitionState(initialState = !appear) }
    state.targetState = visible
    val medium = FintrackTheme.motion.medium
    AnimatedVisibility(
        visibleState = state,
        enter =
            fadeIn(tween(medium.durationMillis, easing = medium.easing)) +
                expandVertically(tween(medium.durationMillis, easing = medium.easing)),
        exit =
            fadeOut(tween(medium.durationMillis, easing = medium.easing)) +
                shrinkVertically(tween(medium.durationMillis, easing = medium.easing)),
    ) {
        content()
    }
    LaunchedEffect(state.isIdle, visible) {
        if (!visible && state.isIdle) onGone()
    }
}
