package com.meqlause.fintrack.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

// FR-123, S-12: stateful entry of Tentang; reads the installed version once per composition
@Composable
internal fun AboutRoute(onBack: () -> Unit) {
    val context = LocalContext.current
    val version = remember(context) { readVersionName(context) }
    AboutScreen(version = version, onBack = onBack)
}
