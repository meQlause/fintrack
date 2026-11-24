package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: the small grey label above a sheet input, as the prototype .flabel
@Composable
fun SheetFieldLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = FintrackScreenText.label(),
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = modifier.padding(top = FintrackTheme.spacing.medium, bottom = FintrackTheme.spacing.xSmall),
    )
}
