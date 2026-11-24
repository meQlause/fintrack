package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.meqlause.fintrack.core.designsystem.component.EmptyIllustrated
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.transaction.TransactionRowGroup

// FR-043, S-03: the five most recent transactions; a row opens its transaction and "Lihat semua" opens Riwayat
@Composable
internal fun HomeRecentList(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        RecentHeader(onOpenHistory = callbacks.onOpenHistory)
        if (state.recent.isEmpty()) {
            EmptyIllustrated(
                title = stringResource(R.string.home_empty_title),
                hint = stringResource(R.string.home_empty_body),
            )
        } else {
            TransactionRowGroup(
                rows = state.recent.map { it.toParams() },
                onClick = { index -> callbacks.onOpenTransaction(state.recent[index].transactionId) },
            )
        }
    }
}

// FR-043: the "Transaksi terakhir" caption and the "Lihat semua" link that opens Riwayat
@Composable
private fun RecentHeader(onOpenHistory: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = FintrackTheme.spacing.xxSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.home_recent_title),
            style = FintrackTheme.typography.caption,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.home_recent_all),
            style = FintrackTheme.typography.captionWide.copy(fontWeight = FontWeight.Bold),
            color = FintrackTheme.colors.brands.primary,
            modifier =
                Modifier
                    .clickable(role = Role.Button, onClick = onOpenHistory)
                    .padding(vertical = FintrackTheme.spacing.xSmall),
        )
    }
}
