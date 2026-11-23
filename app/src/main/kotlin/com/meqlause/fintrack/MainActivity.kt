package com.meqlause.fintrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.meqlause.fintrack.lock.AppLockGate
import com.meqlause.fintrack.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// NFR-001: portrait-only launcher activity; hosts the navigation shell (DOC-09) inside the stored theme (FR-120)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // FR-102: the lock gate decides when the PIN cover is drawn over the shell
    @Inject lateinit var appLockGate: AppLockGate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                FintrackApp(lockGate = appLockGate)
            }
        }
    }
}
