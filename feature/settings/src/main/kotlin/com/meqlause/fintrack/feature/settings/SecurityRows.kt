package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-100: PIN is a switch, turning it on starts the setup and turning it off asks for the current PIN
@Composable
internal fun SecurityRows(
    state: SettingsUiState,
    callbacks: SettingsCallbacks,
) {
    SwitchRow(
        label = stringResource(R.string.settings_pin_label),
        checked = state.pinEnabled,
        onCheckedChange = { on -> if (on) callbacks.onOpenPinSetup() else callbacks.onOpenPinRemove() },
    )
    if (state.pinEnabled) {
        SettingsRow(
            label = stringResource(R.string.settings_pin_change_label),
            value = "",
            onClick = callbacks.onOpenPinChange,
            showDivider = true,
        )
    }
    if (state.pinEnabled && state.biometricSupported) {
        SwitchRow(
            label = stringResource(R.string.settings_biometric_label),
            checked = state.biometricEnabled,
            onCheckedChange = callbacks.onToggleBiometric,
            showDivider = true,
        )
    }
    SwitchRow(
        label = stringResource(R.string.settings_hide_recents_label),
        checked = state.hideFromRecents,
        onCheckedChange = callbacks.onToggleHideFromRecents,
        showDivider = true,
    )
}

// FR-123: "Offline · v0.1" under the last card; tapping it opens Tentang Fintrack
@Composable
internal fun SettingsFooter(
    version: String,
    onOpenAbout: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = FintrackScreenSizes.footerTop)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onOpenAbout),
            horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.pillGap, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(DesignR.drawable.ic_shield),
                contentDescription = null,
                tint = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.size(FintrackScreenSizes.footerGlyph),
            )
            Text(
                text = stringResource(R.string.settings_footer, version),
                style = FintrackScreenText.style(size = 12f, weight = 500),
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
    }
}
