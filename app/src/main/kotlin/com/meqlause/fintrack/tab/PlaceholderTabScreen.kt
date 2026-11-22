package com.meqlause.fintrack.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackLogo
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.component.IconTile
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.navigation.MainTab

// DOC-09: placeholder tab body with the shared tab header; real content arrives with each feature (P4)
@Composable
fun PlaceholderTabScreen(
    mainTab: MainTab,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(FintrackTheme.colors.surfaces.background)
                .padding(horizontal = FintrackTheme.spacing.large, vertical = FintrackTheme.spacing.large),
    ) {
        FintrackTabHeader(title = stringResource(mainTab.titleRes)) {
            TabMark(mainTab = mainTab)
        }
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
