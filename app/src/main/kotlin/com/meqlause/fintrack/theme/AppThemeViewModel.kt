package com.meqlause.fintrack.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val STOP_TIMEOUT_MS = 5_000L

// FR-120: exposes the stored theme choice so the whole app follows it
@HiltViewModel
internal class AppThemeViewModel
    @Inject
    constructor(
        settings: SettingsRepository,
    ) : ViewModel() {
        // FR-120: SYSTEM until the stored choice arrives, then every change from S-12
        val themeMode: StateFlow<ThemeMode> =
            settings
                .observe()
                .map { it.preferences.themeMode }
                .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.SYSTEM)
    }
