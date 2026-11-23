package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype .lbl heading over one ScreenCard; the group sits 20 px below the block above
@Composable
fun ScreenGroup(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.padding(top = FintrackScreenSizes.groupGap)) {
        Text(
            text = label,
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
            modifier =
                Modifier.padding(
                    start = FintrackScreenSizes.groupLabelInset,
                    end = FintrackScreenSizes.groupLabelInset,
                    bottom = FintrackScreenSizes.groupLabelBottom,
                ),
        )
        ScreenCard(content = content)
    }
}
