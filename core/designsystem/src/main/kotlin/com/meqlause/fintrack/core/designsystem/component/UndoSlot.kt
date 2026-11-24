package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.toSpec

// DOC-08, DOC-09: the bar shows while [removed] is not null and a new value restarts its countdown; it grows in and
// fades with a delete and shrinks away when the window ends
@Composable
fun UndoSlot(
    removed: Any?,
    message: String,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val motion = FintrackTheme.motion
    AnimatedVisibility(
        visible = removed != null,
        modifier = modifier,
        enter = fadeIn(motion.medium.toSpec()) + expandVertically(motion.medium.toSpec()),
        exit = fadeOut(motion.medium.toSpec()) + shrinkVertically(motion.medium.toSpec()),
    ) {
        UndoSnackbar(message = message, onUndo = onUndo, restartKey = removed)
    }
}
