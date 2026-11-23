package com.meqlause.fintrack.feature.credit.instalment

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.credit.R

// FR-142: the card name and the monthly portion of a plan, as "BCA Visa · Rp500.000/bln"
@Composable
internal fun InstalmentSubtitle(
    instalmentRow: InstalmentRow,
    modifier: Modifier = Modifier,
) {
    val monthly = stringResource(R.string.instalment_monthly, MoneyFormatter.format(instalmentRow.monthly))
    Text(
        text = "${instalmentRow.cardName} · $monthly",
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.texts.onSurfaceVariant,
        modifier = modifier,
    )
}
