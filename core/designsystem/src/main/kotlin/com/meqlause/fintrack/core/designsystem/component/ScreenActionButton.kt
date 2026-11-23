package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype .card button, 56 px tall, a coloured 18 px glyph and a 13.5 px bold label side by side
@Composable
fun ScreenActionButton(
    text: String,
    iconRes: Int,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScreenCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().height(FintrackScreenSizes.actionHeight).clickable(onClick = onClick),
            horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(FintrackScreenSizes.actionGlyph),
            )
            Text(
                text = text,
                style = FintrackScreenText.style(size = 13.5f, weight = 700),
                color = FintrackTheme.colors.texts.onSurface,
            )
        }
    }
}
