package com.meqlause.fintrack.lock

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.meqlause.fintrack.core.common.coroutines.ApplicationScope
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.core.domain.usecase.ShouldLockUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

// FR-102, S-02: locks the app on cold start and when it returns from the background after the timeout
@Singleton
class AppLockGate
    @Inject
    constructor(
        private val settings: SettingsRepository,
        private val shouldLock: ShouldLockUseCase,
        private val clock: Clock,
        @param:ApplicationScope private val scope: CoroutineScope,
    ) : DefaultLifecycleObserver {
        private val lockState = MutableStateFlow<Boolean?>(null)
        private var backgroundedAt: Instant = Instant.EPOCH

        /** FR-102: null until the settings are read, true while the lock screen must cover the app. */
        public val isLocked: StateFlow<Boolean?> = lockState.asStateFlow()

        /** FR-102: the time the app left the foreground is kept for the timeout check. */
        override fun onStop(owner: LifecycleOwner) {
            backgroundedAt = clock.instant()
        }

        /** FR-102: a cold start or a return after the timeout locks the app when a PIN exists. */
        override fun onStart(owner: LifecycleOwner) {
            scope.launch {
                val security = settings.observe().first().security
                if (shouldLock(security, backgroundedAt, clock.instant())) {
                    lockState.value = true
                } else if (lockState.value == null) {
                    lockState.value = false
                }
            }
        }

        /** FR-100: the correct PIN was entered; the app is shown again. */
        public fun unlock() {
            lockState.value = false
        }
    }
