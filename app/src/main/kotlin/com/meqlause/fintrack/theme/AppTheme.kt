package com.meqlause.fintrack.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.ThemeMode

// FR-120: the app theme follows the stored SYSTEM, LIGHT or DARK choice
@Composable
internal fun AppTheme(content: @Composable () -> Unit) {
    val viewModel: AppThemeViewModel = hiltViewModel()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val darkTheme =
        when (themeMode) {
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }
    FintrackTheme(darkTheme = darkTheme, content = content)
}
