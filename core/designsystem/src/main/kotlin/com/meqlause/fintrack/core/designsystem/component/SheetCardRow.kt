package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: the white rounded row of a sheet form, with a hairline border; it is tappable when onClick is given
@Composable
fun SheetCardRow(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val shape = FintrackTheme.shapes.medium
    val colors = FintrackTheme.colors
    val press = onClick?.let { Modifier.clickable(role = Role.Button, onClick = it) } ?: Modifier
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, colors.surfaces.outline, shape)
                .then(press)
                .heightIn(min = FintrackScreenSizes.fieldHeight)
                .padding(horizontal = FintrackTheme.spacing.medium, vertical = FintrackScreenSizes.pickRowVertical),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
        content = content,
    )
}
