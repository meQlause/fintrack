package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackLogo
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.R as DesignR

// DOC-09, S-03: the Beranda header: logo, the moon or sun theme toggle, the bell with a dot and the settings sliders
@Composable
internal fun HomeHeader(
    hasPending: Boolean,
    callbacks: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    val isDark = FintrackTheme.isDark
    FintrackTabHeader(
        title = stringResource(R.string.home_title),
        modifier = modifier,
        leading = { FintrackLogo() },
        trailing = {
            Row(modifier = Modifier.offset(x = FintrackScreenSizes.sheetCloseShift)) {
                HomeHeaderButton(
                    iconRes = if (isDark) DesignR.drawable.ic_sun else DesignR.drawable.ic_moon,
                    description = stringResource(R.string.home_theme_description),
                    onClick = { callbacks.onToggleTheme(isDark) },
                )
                HomeHeaderButton(
                    iconRes = DesignR.drawable.ic_bell,
                    description = stringResource(R.string.home_inbox_description),
                    onClick = callbacks.onOpenInbox,
                    showDot = hasPending,
                )
                HomeHeaderButton(
                    iconRes = DesignR.drawable.ic_sliders,
                    description = stringResource(R.string.home_settings_description),
                    onClick = callbacks.onOpenSettings,
                )
            }
        },
    )
}
