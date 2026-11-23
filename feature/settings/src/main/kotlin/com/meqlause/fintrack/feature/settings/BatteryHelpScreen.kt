package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-07, S-12: stateless battery help page; the status and the OEM steps come from the route
@Composable
internal fun BatteryHelpScreen(
    optimisationIgnored: Boolean,
    guide: BatteryGuide,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        FintrackTabHeader(
            title = stringResource(R.string.battery_title),
            leading = { BackLabel(onBack = onBack) },
        )
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
        ) {
            BatteryStatusText(optimisationIgnored = optimisationIgnored)
            Text(
                text = stringResource(R.string.battery_intro),
                style = FintrackTheme.typography.bodyLarge,
                color = FintrackTheme.colors.texts.onSurface,
            )
            FintrackButton(
                text = stringResource(R.string.battery_open_button),
                onClick = onOpenSettings,
            )
            BatteryGuideSteps(guide = guide)
        }
    }
}

// DOC-07: says whether the system may still stop Fintrack in the background
@Composable
internal fun BatteryStatusText(optimisationIgnored: Boolean) {
    val label =
        if (optimisationIgnored) {
            R.string.battery_status_unrestricted
        } else {
            R.string.battery_status_restricted
        }
    Text(
        text = stringResource(label),
        style = FintrackTheme.typography.titleMedium,
        color = FintrackTheme.colors.texts.onSurface,
    )
}

// DOC-07: the maker's name above its numbered steps
@Composable
internal fun BatteryGuideSteps(guide: BatteryGuide) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        Text(
            text = stringResource(R.string.battery_steps_title, stringResource(guide.brand)),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        guide.steps.forEachIndexed { index, step ->
            Text(
                text = stringResource(R.string.battery_step_format, index + 1, stringResource(step)),
                style = FintrackTheme.typography.bodyLarge,
                color = FintrackTheme.colors.texts.onSurface,
            )
        }
    }
}
