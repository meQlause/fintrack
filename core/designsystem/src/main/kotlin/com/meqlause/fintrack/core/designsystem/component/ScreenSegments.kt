package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype two-way switch, a 40 px grey track with a white pill under the selected label
@Composable
fun ScreenSegments(
    labels: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(FintrackScreenSizes.segmentTall)
                .clip(RoundedCornerShape(FintrackScreenSizes.segmentCorner))
                .background(FintrackTheme.colors.surfaces.surfaceVariant)
                .padding(FintrackScreenSizes.segmentInset),
    ) {
        labels.forEachIndexed { index, label ->
            val on = index == selected
            val colors = FintrackTheme.colors
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(FintrackScreenSizes.segmentPillCorner))
                        .background(if (on) colors.surfaces.surface else colors.surfaces.surfaceVariant)
                        .clickable { onSelect(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = FintrackScreenText.style(size = 13f, weight = 600),
                    color = if (on) colors.texts.onSurface else colors.texts.onSurfaceVariant,
                )
            }
        }
    }
}
