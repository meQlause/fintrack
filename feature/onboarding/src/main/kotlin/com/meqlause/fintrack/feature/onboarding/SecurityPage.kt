package com.meqlause.fintrack.feature.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

/** S-01, FR-003: page 4, the optional PIN offer; the PIN itself is set up outside onboarding. */
@Composable
internal fun SecurityPage(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        PageIntro(
            title = stringResource(R.string.onboarding_security_title),
            body = stringResource(R.string.onboarding_security_body),
        )
    }
}
