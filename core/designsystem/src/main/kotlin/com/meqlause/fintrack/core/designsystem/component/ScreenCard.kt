package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype .card, an 18 px rounded surface with a hairline; rows inside clip to it and carry no padding
@Composable
fun ScreenCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = FintrackScreenSizes.cardCorner
    Column(
        modifier =
            modifier
                .clip(shape)
                .background(FintrackTheme.colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, FintrackTheme.colors.surfaces.outline, shape)
                .padding(FintrackTheme.sizes.hairline),
        content = content,
    )
}
