package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// S-04 and 09: undo bar shown for 5 s after a delete or dismiss; the host owns the timer and visibility
@Composable
fun UndoSnackbar(
    message: String,
    onUndo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    Row(
        modifier =
            modifier
                .height(FintrackTheme.sizes.snackbarHeight)
                .clip(FintrackTheme.shapes.full)
                .background(colors.texts.onSurface)
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
