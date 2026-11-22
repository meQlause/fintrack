package com.meqlause.fintrack.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

/** S-01, FR-003: page 3, notification detection and the access state reported on return from settings. */
@Composable
internal fun NotificationPage(
    isGranted: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        PageIntro(
            title = stringResource(R.string.onboarding_notification_title),
            body = stringResource(R.string.onboarding_notification_body),
        )
        Text(
            text = accessText(isGranted),
            style = FintrackTheme.typography.labelLarge,
            color = accessColor(isGranted),
        )
    }
}

@Composable
private fun accessText(isGranted: Boolean): String =
    if (isGranted) {
        stringResource(R.string.onboarding_notification_granted)
    } else {
        stringResource(R.string.onboarding_notification_not_granted)
    }

@Composable
private fun accessColor(isGranted: Boolean) =
    if (isGranted) FintrackTheme.colors.semantics.income else FintrackTheme.colors.texts.onSurfaceVariant
