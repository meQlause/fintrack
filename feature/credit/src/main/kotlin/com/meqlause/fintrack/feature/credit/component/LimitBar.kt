package com.meqlause.fintrack.feature.credit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.credit.R
import com.meqlause.fintrack.feature.credit.detail.CreditDetailUiState

// FR-131, BR-044: outstanding against the credit limit; the bar turns red and says so when over the limit
@Composable
internal fun LimitBar(
    state: CreditDetailUiState.Success,
    modifier: Modifier = Modifier,
) {
    val limit = state.terms.creditLimit.rupiah
    val used = state.outstanding.rupiah
    val fraction = if (limit > 0L) (used.toFloat() / limit.toFloat()).coerceIn(0f, 1f) else 0f
    val over = state.availableLimit.rupiah < 0L
    FintrackCard(modifier = modifier) {
        Text(
            text = stringResource(R.string.credit_limit_title),
            style = FintrackTheme.typography.labelLarge,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = "${MoneyFormatter.format(state.outstanding)} / ${MoneyFormatter.format(state.terms.creditLimit)}",
            style = FintrackTheme.typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(FintrackTheme.spacing.small)
                    .clip(FintrackTheme.shapes.full)
                    .background(FintrackTheme.colors.surfaces.surfaceVariant),
        ) {
            val barColor = if (over) FintrackTheme.colors.semantics.error else FintrackTheme.colors.brands.primary
            Box(
                modifier =
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction)
                        .background(barColor),
            )
        }
        Text(
            text = availableText(state, over),
            style = FintrackTheme.typography.bodyMedium,
            color = if (over) FintrackTheme.colors.semantics.error else FintrackTheme.colors.texts.onSurfaceVariant,
        )
    }
}

@Composable
private fun availableText(
    state: CreditDetailUiState.Success,
    over: Boolean,
): String =
    if (over) {
        stringResource(R.string.credit_limit_exceeded)
    } else {
        stringResource(R.string.credit_limit_available, MoneyFormatter.format(state.availableLimit))
    }
