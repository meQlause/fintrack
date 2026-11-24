package com.meqlause.fintrack.core.designsystem.icon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme

private const val DEFAULT_COLUMNS = 8

// DOC-08, FR-021: the icon or colour choices of a category in rows of [columns], each cell drawn by the caller
@Composable
fun ChoiceGrid(
    keys: List<String>,
    modifier: Modifier = Modifier,
    columns: Int = DEFAULT_COLUMNS,
    cell: @Composable (String) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        keys.chunked(columns).forEach { line ->
            Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
                line.forEach { key -> cell(key) }
            }
        }
    }
}
