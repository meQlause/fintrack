package com.meqlause.fintrack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.meqlause.fintrack.core.domain.repository.SettingsRepository
import com.meqlause.fintrack.lock.AppLockGate
import com.meqlause.fintrack.lock.applyHideFromRecents
import com.meqlause.fintrack.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

// NFR-001: portrait-only launcher activity; hosts the navigation shell (DOC-09) inside the stored theme (FR-120)
// FragmentActivity so the biometric sheet (FR-101) can be hosted
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    // FR-102: the lock gate decides when the PIN cover is drawn over the shell
    @Inject lateinit var appLockGate: AppLockGate

    // FR-104: the stored recents option decides whether the window carries FLAG_SECURE
    @Inject lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            settings.observe().map { it.security.hideFromRecents }.distinctUntilChanged().collect {
                window.applyHideFromRecents(it)
            }
        }
        setContent {
            AppTheme {
                FintrackApp(lockGate = appLockGate)
            }
        }
    }
}
