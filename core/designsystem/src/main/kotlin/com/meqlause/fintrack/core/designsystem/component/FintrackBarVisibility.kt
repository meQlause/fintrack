package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.theme.toSpec

// DOC-09, DOC-08: the bottom bar slides down and fades when it hides and rises back when it returns
@Composable
fun FintrackBarVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val motion = FintrackTheme.motion
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(motion.medium.toSpec()) + slideInVertically(motion.medium.toSpec()) { it },
        exit = fadeOut(motion.medium.toSpec()) + slideOutVertically(motion.medium.toSpec()) { it },
    ) {
        content()
    }
}
