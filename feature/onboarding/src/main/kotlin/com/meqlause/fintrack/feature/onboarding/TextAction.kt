package com.meqlause.fintrack.feature.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

/** S-01: a text-only action on the touch-target height, used for "Lewati" and "Nanti saja". */
@Composable
internal fun TextAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .heightIn(min = FintrackTheme.sizes.touchTarget)
                .clickable(role = Role.Button, onClick = onClick)
                .padding(horizontal = FintrackTheme.spacing.small),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.brands.primary,
        )
    }
}
