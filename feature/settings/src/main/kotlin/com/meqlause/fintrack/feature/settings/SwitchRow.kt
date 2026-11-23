package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.meqlause.fintrack.core.designsystem.component.FintrackSwitch
import com.meqlause.fintrack.core.designsystem.component.ScreenRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-101, FR-104, S-12: a settings row with a label on the left and a switch on the right
@Composable
internal fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean = false,
) {
    ScreenRow(
        showDivider = showDivider,
        contentPadding =
            PaddingValues(
                horizontal = FintrackScreenSizes.rowPaddingHorizontal,
                vertical = FintrackScreenSizes.rowSwitchInset,
            ),
        leading = {},
        trailing = { FintrackSwitch(checked = checked, onCheckedChange = onCheckedChange) },
    ) {
        Text(
            text = label,
            style = FintrackScreenText.style(size = 14f, weight = 500),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}
