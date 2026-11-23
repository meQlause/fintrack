package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-120, S-12: a prototype .row of Pengaturan, 15 px label, muted value and chevron; the divider is its top border
@Composable
internal fun SettingsRow(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = false,
) {
    ScreenRow(
        modifier = modifier,
        showDivider = showDivider,
        onClick = onClick,
        contentPadding = rowPadding(),
        leading = {},
        trailing = {
            if (value.isNotEmpty()) {
                Text(
                    text = value,
                    style = FintrackScreenText.style(size = 13f, weight = 500),
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                )
            }
            Icon(
                painter = painterResource(DesignR.drawable.ic_chevron_right),
                contentDescription = null,
                tint = FintrackTheme.colors.texts.onSurfaceVariant,
                modifier = Modifier.size(FintrackScreenSizes.chevron),
            )
        },
    ) {
        Text(
            text = label,
            style = FintrackScreenText.style(size = 14f, weight = 500),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

private fun rowPadding(): PaddingValues =
    PaddingValues(
        horizontal = FintrackScreenSizes.rowPaddingHorizontal,
        vertical = FintrackScreenSizes.rowPaddingVertical,
    )
