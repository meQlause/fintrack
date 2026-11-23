package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.meqlause.fintrack.core.designsystem.R
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: header of a pushed screen, a 40 px back button, an 18 px bold title and an optional grey badge
@Composable
fun ScreenBackHeader(
    title: String,
    backLabel: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(
                    start = FintrackTheme.spacing.medium,
                    end = FintrackTheme.spacing.medium,
                    top = FintrackScreenSizes.screenTop,
                    bottom = FintrackTheme.spacing.xSmall,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall),
    ) {
        BackButton(backLabel = backLabel, onBack = onBack)
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            style = FintrackScreenText.style(size = 18f, weight = 700),
            color = FintrackTheme.colors.texts.onSurface,
        )
        badge?.let { Badge(text = it) }
        Spacer(modifier = Modifier.weight(1f))
        trailing()
    }
}

@Composable
private fun Badge(text: String) {
    Text(
        text = text,
        style = FintrackScreenText.style(size = 11f, weight = 700),
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier =
            Modifier
                .clip(RoundedCornerShape(FintrackScreenSizes.badgeCorner))
                .background(FintrackTheme.colors.surfaces.surfaceVariant)
                .padding(horizontal = FintrackTheme.spacing.small, vertical = FintrackScreenSizes.badgePadding),
    )
}

@Composable
private fun BackButton(
    backLabel: String,
    onBack: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(FintrackScreenSizes.iconButton)
                .clip(CircleShape)
                .clickable(onClick = onBack),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_back),
            contentDescription = backLabel,
            tint = FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.size(FintrackScreenSizes.backGlyph),
        )
    }
}
