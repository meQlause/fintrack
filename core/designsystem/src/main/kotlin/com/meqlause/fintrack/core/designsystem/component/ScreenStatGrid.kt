package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: one cell of a stat grid; a null color shows the value in the normal text colour
data class ScreenStat(
    val label: String,
    val value: String,
    val color: Color? = null,
)

// DOC-08: prototype card with equal columns split by 1 px dividers, a label over a 15 px bold value
@Composable
fun ScreenStatGrid(
    stats: List<ScreenStat>,
    modifier: Modifier = Modifier,
) {
    ScreenCard(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            stats.forEachIndexed { index, stat ->
                if (index > 0) {
                    VerticalDivider(
                        thickness = FintrackTheme.sizes.hairline,
                        color = FintrackTheme.colors.surfaces.outline,
                    )
                }
                StatCell(stat = stat, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatCell(
    stat: ScreenStat,
    modifier: Modifier,
) {
    Column(
        modifier =
            modifier.padding(
                horizontal = FintrackScreenSizes.rowPaddingHorizontal,
                vertical = FintrackScreenSizes.rowPaddingVertical,
            ),
    ) {
        Text(
            text = stat.label,
            style = FintrackScreenText.label(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = stat.value,
            style = FintrackScreenText.number(size = 15f, weight = 700),
            color = stat.color ?: FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
    }
}
