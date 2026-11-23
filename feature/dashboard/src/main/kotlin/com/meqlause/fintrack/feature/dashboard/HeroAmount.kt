package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

private const val CURRENCY_PREFIX = "Rp"

// DOC-08, FR-040: the hero balance as the mockup draws it, a small muted Rp followed by large digits on one baseline
@Composable
internal fun HeroAmount(
    formatted: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = CURRENCY_PREFIX,
            modifier = Modifier.alignByBaseline(),
            style = FintrackTheme.typography.prefixDisplay,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = formatted.replace(CURRENCY_PREFIX, ""),
            modifier = Modifier.alignByBaseline(),
            style = FintrackTheme.typography.numberDisplay,
            color = FintrackTheme.colors.texts.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
