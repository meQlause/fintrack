package com.meqlause.fintrack.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meqlause.fintrack.core.domain.usecase.ShouldShowOnboardingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import javax.inject.Inject

/** FR-001: decides the start destination of the NavHost before any screen is shown. */
@HiltViewModel
class AppStartViewModel
    @Inject
    constructor(
        shouldShowOnboarding: ShouldShowOnboardingUseCase,
    ) : ViewModel() {
        /** FR-001: the first stored answer only; later changes must not rebuild the NavHost mid-onboarding. */
        val showOnboarding: StateFlow<Boolean?> =
            shouldShowOnboarding()
                .take(1)
                .map<Boolean, Boolean?> { it }
                .stateIn(viewModelScope, SharingStarted.Eagerly, null)
    }
