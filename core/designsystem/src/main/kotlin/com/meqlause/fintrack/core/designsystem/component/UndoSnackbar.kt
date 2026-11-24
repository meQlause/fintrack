package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-04 and 09: undo bar shown for the undo window after a delete; a line under it shrinks to show the time left
@Composable
fun UndoSnackbar(
    message: String,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier,
    restartKey: Any? = null,
) {
    val colors = FintrackTheme.colors
    Column(modifier = modifier.clip(FintrackTheme.shapes.full).background(colors.texts.onSurface)) {
        UndoRow(message = message, onUndo = onUndo)
        UndoCountdown(restartKey = restartKey)
    }
}

// S-04 and 09: the line runs out over the undo window; a new delete passes a new key and starts it again
@Composable
private fun UndoCountdown(restartKey: Any?) {
    val left = remember { Animatable(initialValue = 1f) }
    LaunchedEffect(restartKey) {
        left.snapTo(1f)
        left.animateTo(targetValue = 0f, animationSpec = tween(UndoWindow.MILLIS.toInt(), easing = LinearEasing))
    }
    val color = FintrackTheme.colors.brands.primaryContainer
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(FintrackTheme.sizes.selectionRing)
                .drawBehind { drawRect(color, size = Size(width = size.width * left.value, height = size.height)) },
    )
}

@Composable
private fun UndoRow(
    message: String,
    onUndo: () -> Unit,
) {
    val colors = FintrackTheme.colors
    Row(
        modifier =
            Modifier
                .heightIn(min = FintrackTheme.sizes.snackbarHeight)
                .padding(start = FintrackTheme.spacing.large, end = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            style = FintrackTheme.typography.labelLarge,
            color = colors.surfaces.background,
        )
        Spacer(modifier = Modifier.width(FintrackTheme.spacing.medium))
        Text(
            text = stringResource(R.string.component_undo),
            style = FintrackTheme.typography.labelLarge,
            color = colors.brands.primaryContainer,
            modifier =
                Modifier
                    .clip(FintrackTheme.shapes.full)
                    .clickable(role = Role.Button, onClick = onUndo)
                    .padding(horizontal = FintrackTheme.spacing.medium),
        )
    }
}

@Composable
internal fun UndoSnackbarSample() {
    UndoSnackbar(message = stringResource(R.string.sample_undo_message), onUndo = {})
}

@Preview(name = "UndoSnackbar light", showBackground = true)
@Composable
internal fun UndoSnackbarLightPreview() {
    ComponentCanvas(darkTheme = false) { UndoSnackbarSample() }
}

@Preview(name = "UndoSnackbar dark", showBackground = true)
@Composable
internal fun UndoSnackbarDarkPreview() {
    ComponentCanvas(darkTheme = true) { UndoSnackbarSample() }
}
