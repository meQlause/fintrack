package com.meqlause.fintrack.tab

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.R
import com.meqlause.fintrack.core.designsystem.component.FintrackLogo
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.component.IconTile
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.navigation.MainTab
import com.meqlause.fintrack.core.designsystem.R as DesignR

// DOC-09: placeholder tab body with the shared tab header; real content arrives with each feature (P4)
@Composable
fun PlaceholderTabScreen(
    mainTab: MainTab,
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large, vertical = FintrackTheme.spacing.large),
    ) {
        FintrackTabHeader(
            title = stringResource(mainTab.titleRes),
            leading = { TabMark(mainTab = mainTab) },
            trailing = { if (mainTab == MainTab.HOME) SettingsButton(onClick = onSettingsClick) },
        )
    }
}

// DOC-09, S-12: the gear-style entry to Pengaturan in the Beranda header
@Composable
private fun SettingsButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .size(FintrackTheme.sizes.touchTarget)
                .clip(FintrackTheme.shapes.full)
                .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(DesignR.drawable.ic_sliders),
            contentDescription = stringResource(R.string.home_settings_description),
            colorFilter = ColorFilter.tint(FintrackTheme.colors.texts.onSurface),
            modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
        )
    }
}

// DOC-09: Beranda shows the logo; the other tabs show their icon on a soft-teal tile
@Composable
private fun TabMark(mainTab: MainTab) {
    if (mainTab == MainTab.HOME) {
        FintrackLogo()
    } else {
        IconTile(iconRes = mainTab.iconRes)
    }
}
