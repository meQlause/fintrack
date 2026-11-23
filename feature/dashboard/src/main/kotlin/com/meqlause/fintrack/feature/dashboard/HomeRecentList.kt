package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.meqlause.fintrack.core.designsystem.component.EmptyState
import com.meqlause.fintrack.core.designsystem.component.EmptyStateCopy
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.transaction.TransactionRow
import com.meqlause.fintrack.core.ui.transaction.TransactionRowCallbacks
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-043, S-03: the five most recent transactions; a row opens its transaction and "Lihat semua" opens Riwayat
@Composable
internal fun HomeRecentList(
    state: HomeUiState.Success,
    callbacks: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.home_recent_title),
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
            Text(
                text = stringResource(R.string.home_recent_all),
                style = FintrackTheme.typography.labelLarge,
                color = FintrackTheme.colors.brands.primary,
                modifier =
                    Modifier
                        .clip(FintrackTheme.shapes.full)
                        .clickable(role = Role.Button, onClick = callbacks.onOpenHistory)
                        .padding(horizontal = FintrackTheme.spacing.small, vertical = FintrackTheme.spacing.xSmall),
            )
        }
        if (state.recent.isEmpty()) {
            EmptyState(
                copy =
                    EmptyStateCopy(
                        title = stringResource(R.string.home_empty_title),
                        body = stringResource(R.string.home_empty_body),
                    ),
                iconRes = DesignR.drawable.ic_history,
            )
        } else {
            state.recent.forEach { row ->
                TransactionRow(
                    params = row.toParams(),
                    callbacks = TransactionRowCallbacks(onClick = { callbacks.onOpenTransaction(row.transactionId) }),
                )
            }
        }
    }
}
