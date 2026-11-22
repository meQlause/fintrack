package com.meqlause.fintrack.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// DOC-08: placeholder swatch for the theme screenshot test; replaced by the Fintrack tokens in T2.3
@Composable
fun ThemeSwatch(
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    MaterialTheme(colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()) {
        Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
            Box(Modifier.size(SWATCH_SIZE).background(MaterialTheme.colorScheme.primary))
            Box(Modifier.size(SWATCH_SIZE).background(MaterialTheme.colorScheme.surface))
        }
    }
}

private val SWATCH_SIZE = 48.dp
