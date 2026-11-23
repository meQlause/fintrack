package com.meqlause.fintrack.feature.transaction.history

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackTabHeader
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.transaction.R

// FR-037, S-05: the Riwayat title with the month and date picker and the search toggle
@Composable
internal fun HistoryHeader(
    onCalendarClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    FintrackTabHeader(
        title = stringResource(R.string.history_title),
        leading = {},
        trailing = {
            Row {
                HeaderIcon(R.drawable.ic_calendar, R.string.history_calendar_description, onCalendarClick)
                HeaderIcon(R.drawable.ic_search, R.string.history_search_description, onSearchClick)
            }
        },
    )
}

@Composable
private fun HeaderIcon(
    @DrawableRes iconRes: Int,
    @StringRes description: Int,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick, modifier = Modifier.size(FintrackTheme.sizes.touchTarget)) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(description),
            tint = FintrackTheme.colors.texts.onSurface,
            modifier = Modifier.size(FintrackTheme.sizes.iconMedium),
        )
    }
}
