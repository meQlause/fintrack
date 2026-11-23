package com.meqlause.fintrack.feature.settings

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect

// DOC-07, S-12: stateful entry of the battery help page; the status is read again whenever the page resumes
@Composable
internal fun BatteryHelpRoute(onBack: () -> Unit) {
    val context = LocalContext.current
    var ignored by remember { mutableStateOf(isOptimisationIgnored(context)) }
    LifecycleResumeEffect(Unit) {
        ignored = isOptimisationIgnored(context)
        onPauseOrDispose {}
    }
    BatteryHelpScreen(
        optimisationIgnored = ignored,
        guide = batteryGuideFor(Build.MANUFACTURER),
        onOpenSettings = { context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)) },
        onBack = onBack,
    )
}
