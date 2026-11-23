package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-09: the logo header of Beranda above a tile header of another tab
@Composable
internal fun TabHeaderSample() {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.large)) {
        FintrackTabHeader(
            title = stringResource(R.string.token_sample_brand),
            leading = { FintrackLogo() },
        )
        FintrackTabHeader(
            title = stringResource(R.string.sample_tab_history),
            leading = { IconTile(iconRes = R.drawable.ic_history) },
        )
    }
}

@Preview(name = "TabHeader light", showBackground = true)
@Composable
internal fun TabHeaderLightPreview() {
    ComponentCanvas(darkTheme = false) { TabHeaderSample() }
}

@Preview(name = "TabHeader dark", showBackground = true)
@Composable
internal fun TabHeaderDarkPreview() {
    ComponentCanvas(darkTheme = true) { TabHeaderSample() }
}
