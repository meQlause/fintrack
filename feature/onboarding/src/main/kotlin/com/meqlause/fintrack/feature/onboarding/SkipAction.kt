package com.meqlause.fintrack.feature.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

/** S-01, FR-001: "Lewati" in the top-right corner; it stays in the layout so the pages do not jump. */
@Composable
internal fun SkipAction(
    visible: Boolean,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().heightIn(min = FintrackTheme.sizes.touchTarget),
        contentAlignment = Alignment.CenterEnd,
    ) {
        if (visible) {
            TextAction(
                label = stringResource(R.string.onboarding_skip),
                onClick = onSkip,
                modifier = Modifier.wrapContentWidth(),
            )
        }
    }
}
