package com.meqlause.fintrack.core.designsystem.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
    modifier: Modifier = Modifier,
    showDivider: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    contentPadding: PaddingValues =
        PaddingValues(
            horizontal = FintrackScreenSizes.rowPaddingHorizontal,
            vertical = FintrackScreenSizes.rowPaddingVertical,
        ),
    leading: @Composable () -> Unit,
    trailing: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        if (showDivider) {
            HorizontalDivider(
                thickness = FintrackTheme.sizes.hairline,
                color = FintrackTheme.colors.surfaces.outline,
            )
        }
        val press = onClick?.let { Modifier.combinedClickable(onClick = it, onLongClick = onLongClick) } ?: Modifier
        Row(
            modifier = Modifier.fillMaxWidth().then(press).padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FintrackScreenSizes.rowGap),
        ) {
            leading()
            Column(modifier = Modifier.weight(1f), content = content)
            trailing()
        }
    }
}
