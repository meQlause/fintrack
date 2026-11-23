package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

// DOC-08: prototype .row, leading mark, a growing text column and trailing content; the divider is its border-top
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenRow(
    slots: RowSlots,
    modifier: Modifier = Modifier,
    style: RowStyle = RowStyle(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        if (style.showDivider) {
            HorizontalDivider(
                thickness = FintrackTheme.sizes.hairline,
                color = FintrackTheme.colors.surfaces.outline,
            )
        }
        val press =
            style.onClick?.let { Modifier.combinedClickable(onClick = it, onLongClick = style.onLongClick) } ?: Modifier
        Row(
            modifier = Modifier.fillMaxWidth().then(press).padding(style.contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.rowGap),
        ) {
            slots.leading()
            Column(modifier = Modifier.weight(1f), content = content)
            slots.trailing()
        }
    }
}
