package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackLogo
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader

// DOC-09, S-03: the Beranda header keeps the logo and the gear that opens Pengaturan (S-12)
@Composable
internal fun HomeHeader(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FintrackTabHeader(
        title = stringResource(R.string.home_title),
        modifier = modifier,
        leading = { FintrackLogo() },
        trailing = { HomeSettingsButton(onClick = onSettingsClick) },
    )
}
