package com.meqlause.fintrack.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

/** S-01: the title and explanation at the top of a page. */
@Composable
internal fun PageIntro(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        Text(text = title, style = FintrackTheme.typography.headlineSmall, color = FintrackTheme.colors.texts.onSurface)
        Text(
            text = body,
            style = FintrackTheme.typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}
