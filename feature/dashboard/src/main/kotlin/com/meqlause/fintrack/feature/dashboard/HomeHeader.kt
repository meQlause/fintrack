package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackLogo
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader

// DOC-09, S-03: the Beranda header keeps the logo, the bell that opens Kotak Masuk and the gear for Pengaturan (S-12)
@Composable
internal fun HomeHeader(
    onSettingsClick: () -> Unit,
    onInboxClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FintrackTabHeader(
        title = stringResource(R.string.home_title),
        modifier = modifier,
        leading = { FintrackLogo() },
        trailing = {
            Row {
                HomeInboxButton(onClick = onInboxClick)
                HomeSettingsButton(onClick = onSettingsClick)
            }
        },
    )
}
