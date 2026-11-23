package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-084, S-03: the bell that opens Kotak Masuk; the badge is a later step (see the inbox report)
@Composable
internal fun HomeInboxButton(
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
            painter = painterResource(R.drawable.ic_bell),
            contentDescription = stringResource(R.string.home_inbox_description),
            modifier = Modifier.size(FintrackTheme.sizes.iconMedium),
        )
    }
}
