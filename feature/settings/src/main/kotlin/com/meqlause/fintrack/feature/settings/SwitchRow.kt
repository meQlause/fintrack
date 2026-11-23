package com.meqlause.fintrack.feature.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.component.FintrackSwitch
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// FR-101, FR-104, S-12: a settings row with a label on the left and a switch on the right
@Composable
internal fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = FintrackTheme.typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
        FintrackSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
