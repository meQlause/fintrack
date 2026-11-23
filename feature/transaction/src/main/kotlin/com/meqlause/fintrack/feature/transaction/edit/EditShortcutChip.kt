package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// S-04, FR-039, DOC-08: the white "Pintasan" pill with a bolt that opens the shortcut drawer
@Composable
internal fun ShortcutChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = FintrackTheme.shapes.full
    Row(
        modifier =
            modifier
                .height(FintrackScreenSizes.pillTall)
                .clip(shape)
                .background(FintrackTheme.colors.surfaces.surface)
                .border(FintrackTheme.sizes.hairline, FintrackTheme.colors.surfaces.outline, shape)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = FintrackTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.actionTallGap),
    ) {
        Icon(
            painter = painterResource(DesignR.drawable.ic_bolt),
            contentDescription = null,
            tint = FintrackTheme.colors.brands.primary,
            modifier = Modifier.size(FintrackScreenSizes.tinyIcon),
        )
        Text(
            text = stringResource(R.string.transaction_shortcut_chip),
            style = FintrackScreenText.style(size = 12.5f, weight = 700),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}
