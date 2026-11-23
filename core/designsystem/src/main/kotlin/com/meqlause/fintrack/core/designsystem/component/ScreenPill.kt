package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype .chip, a 32 px pill with a hairline, 12.5 px bold label and an optional 8 px colour dot
@Composable
fun ScreenPill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dot: Color? = null,
    contentColor: Color = FintrackTheme.colors.texts.onSurface,
    selected: Boolean = false,
    height: Dp = FintrackScreenSizes.pillTall,
) {
    val shape = CircleShape
    val colors = FintrackTheme.colors
    val fill = if (selected) colors.texts.onSurface else colors.surfaces.surface
    val border = if (selected) colors.texts.onSurface else colors.surfaces.outline
    val textColor = if (selected) colors.surfaces.background else contentColor
    Row(
        modifier =
            modifier
                .height(height)
                .clip(shape)
                .background(fill)
                .border(FintrackTheme.sizes.hairline, border, shape)
                .clickable(onClick = onClick)
                .padding(horizontal = FintrackTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small - FintrackTheme.spacing.xxSmall),
    ) {
        if (dot != null) {
            Row(modifier = Modifier.size(FintrackScreenSizes.pillDot).clip(CircleShape).background(dot)) {}
        }
        Text(
            text = label,
            style = FintrackScreenText.style(size = 12.5f, weight = 600),
            color = textColor,
            maxLines = 1,
        )
    }
}
