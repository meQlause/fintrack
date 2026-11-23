package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.R as DesignR

// DOC-09, S-12: the gear in the Beranda header that opens Pengaturan
@Composable
internal fun HomeSettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
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
