package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.HistoryView
import com.meqlause.fintrack.feature.transaction.R

// S-05: the "Daftar | Statistik" switch that sits under the month strip
@Composable
internal fun HistoryViewSwitch(
    view: HistoryView,
    onPick: (HistoryView) -> Unit,
) {
    HistorySegmented(
        options =
            listOf(
                HistorySegmentOption(HistoryView.LIST, stringResource(R.string.history_view_list)),
                HistorySegmentOption(HistoryView.STATISTICS, stringResource(R.string.history_view_statistics)),
            ),
        selected = view,
        onPick = onPick,
        modifier = Modifier.padding(top = FintrackTheme.spacing.small),
    )
}
