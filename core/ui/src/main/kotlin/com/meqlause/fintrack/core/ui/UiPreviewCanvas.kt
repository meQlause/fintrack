package com.meqlause.fintrack.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: themed backdrop for previews and screenshots of shared UI, with the screen gutter around the content
@Composable
internal fun UiPreviewCanvas(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    FintrackTheme(darkTheme = darkTheme) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(FintrackTheme.colors.surfaces.background)
                    .padding(FintrackTheme.spacing.large),
        ) {
            content()
        }
    }
}
