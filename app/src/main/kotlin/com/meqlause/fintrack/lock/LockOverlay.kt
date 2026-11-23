package com.meqlause.fintrack.lock

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.lock.LockRoute

// FR-102, S-02: an opaque cover above the app while locked; back does nothing so the cover cannot be skipped
@Composable
fun LockOverlay(
    locked: Boolean?,
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (locked) {
        true -> {
            BackHandler(enabled = true) {}
            // NFR-010: the lock screen keeps its keypad clear of the system bars on edge-to-edge devices
            LockRoute(
                onUnlocked = onUnlocked,
                modifier = modifier.background(FintrackTheme.colors.surfaces.background).systemBarsPadding(),
            )
        }

        null -> {
            Box(modifier = modifier.fillMaxSize().background(FintrackTheme.colors.surfaces.background))
        }

        false -> {
            Unit
        }
    }
}
