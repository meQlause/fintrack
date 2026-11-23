package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-09, S-03: one 40 px round icon button of the Beranda header; an optional dot marks pending items
@Composable
internal fun HomeHeaderButton(
    iconRes: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDot: Boolean = false,
) {
    Box(
        modifier =
            modifier
                .size(FintrackScreenSizes.iconButton)
                .clip(FintrackTheme.shapes.full)
                .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = description,
            colorFilter = ColorFilter.tint(FintrackTheme.colors.texts.onSurface),
            modifier = Modifier.size(FintrackTheme.sizes.headerGlyph),
        )
        if (showDot) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .offset(x = FintrackScreenSizes.headerDotShift, y = -FintrackScreenSizes.headerDotShift)
                        .size(FintrackScreenSizes.pillDot)
                        .background(FintrackTheme.colors.semantics.expense, CircleShape),
            )
        }
    }
}
