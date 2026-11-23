package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.feature.wallet.R

// FR-150, S-09: the Pinjaman row opens the loan list (S-17)
@Composable
internal fun WalletLoanRow(onOpen: () -> Unit) {
    FintrackCard(onClick = onOpen) {
        Text(
            text = stringResource(R.string.wallet_loans_title),
            style = FintrackTheme.typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Text(
            text = stringResource(R.string.wallet_loans_body),
            style = FintrackTheme.typography.bodyMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}
