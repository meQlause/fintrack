package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.ui.picker.PickerOption

// FR-081, FR-082, FR-090: every action of S-13; defaults do nothing so previews can omit them
internal data class MonitoredAppsCallbacks(
    val onBack: () -> Unit = {},
    val onOpenAccessSettings: () -> Unit = {},
    val onToggle: (String, Boolean) -> Unit = { _, _ -> },
    val onOpenPicker: (String) -> Unit = {},
    val onPickWallet: (PickerOption) -> Unit = {},
    val onDismissPicker: () -> Unit = {},
)
