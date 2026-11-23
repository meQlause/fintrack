package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R

// FR-037, S-05: the search field under the header; it matches note and merchant text
@Composable
internal fun HistorySearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.padding(top = FintrackTheme.spacing.medium),
        singleLine = true,
        shape = FintrackTheme.shapes.medium,
        placeholder = {
            Text(text = stringResource(R.string.history_search_hint), style = FintrackTheme.typography.bodyMedium)
        },
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = FintrackTheme.colors.surfaces.surfaceVariant,
                unfocusedContainerColor = FintrackTheme.colors.surfaces.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
    )
}
