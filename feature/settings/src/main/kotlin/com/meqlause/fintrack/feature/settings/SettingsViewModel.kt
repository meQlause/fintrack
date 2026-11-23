package com.meqlause.fintrack.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val STOP_TIMEOUT_MS = 5_000L

// FR-120, FR-121: drives S-12; every change is written through the settings repository
@HiltViewModel
internal class SettingsViewModel
    @Inject
    constructor(
        private val settings: SettingsRepository,
    ) : ViewModel() {
        private val sheet = MutableStateFlow<SettingsSheet?>(null)

        // S-12: the stored preferences plus the open drawer, emitted again after every change
        val state: StateFlow<SettingsUiState> =
            combine(settings.observe(), sheet) { stored, open ->
                SettingsUiState(
                    themeMode = stored.preferences.themeMode,
                    periodStartDay = stored.preferences.periodStartDay,
                    pinEnabled = stored.security.pinLock.pinHash != null,
                    sheet = open,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), SettingsUiState())

        // S-12: a settings row opens its picker drawer
        fun openSheet(target: SettingsSheet) {
            sheet.value = target
        }

        // S-12: closing a drawer without picking keeps the stored value
        fun dismissSheet() {
            sheet.value = null
        }

        // FR-120: the theme is stored at once and the drawer closes
        fun pickThemeMode(mode: ThemeMode) {
            sheet.value = null
            updatePreferences { it.copy(themeMode = mode) }
        }

        // FR-121: the period start day is stored at once and the drawer closes
        fun pickPeriodStartDay(day: Int) {
            sheet.value = null
            updatePreferences { it.copy(periodStartDay = day) }
        }

        private fun updatePreferences(transform: (Preferences) -> Preferences) {
            viewModelScope.launch {
                settings.update { stored -> stored.copy(preferences = transform(stored.preferences)) }
            }
        }
    }
