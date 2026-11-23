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
import androidx.compose.ui.graphics.takeOrElse
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype .chip, a 32 px pill with a hairline, 12.5 px bold label and an optional 8 px colour dot
@Composable
fun ScreenPill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    look: PillLook = PillLook(),
) {
    val selected = look.selected
    val colors = FintrackTheme.colors
    val fill = if (selected) colors.texts.onSurface else colors.surfaces.surface
    val border = if (selected) colors.texts.onSurface else colors.surfaces.outline
    Row(
        modifier =
            modifier
                .height(look.height)
                .clip(CircleShape)
                .background(fill)
                .border(FintrackTheme.sizes.hairline, border, CircleShape)
                .clickable(onClick = onClick)
                .padding(horizontal = FintrackTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small - FintrackTheme.spacing.xxSmall),
    ) {
        PillContent(label = label, look = look)
    }
}

@Composable
private fun PillContent(
    label: String,
    look: PillLook,
) {
    val colors = FintrackTheme.colors
    val plain = look.contentColor.takeOrElse { colors.texts.onSurface }
    val textColor = if (look.selected) colors.surfaces.background else plain
    if (look.dotColor != null) {
        Row(modifier = Modifier.size(FintrackScreenSizes.pillDot).clip(CircleShape).background(look.dotColor)) {}
    }
    Text(
        text = label,
        style = FintrackScreenText.style(size = 12.5f, weight = 600),
        color = textColor,
        maxLines = 1,
    )
}
