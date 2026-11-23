package com.meqlause.fintrack.feature.credit.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
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
    val colors = FintrackTheme.colors
    ScreenCard(modifier = modifier) {
        Column(modifier = Modifier.padding(FintrackTheme.spacing.large)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(R.string.credit_limit_title),
                    style = FintrackScreenText.label(),
                    color = colors.texts.onSurfaceVariant,
                )
                Text(
                    text = MoneyFormatter.format(state.terms.creditLimit),
                    style = FintrackScreenText.number(size = 12.5f, weight = 600),
                    color = colors.texts.onSurface,
                )
            }
            Box(
                modifier =
                    Modifier
                        .padding(top = FintrackTheme.spacing.medium - FintrackTheme.spacing.xxSmall)
                        .fillMaxWidth()
                        .height(FintrackScreenSizes.limitBar)
                        .clip(RoundedCornerShape(FintrackScreenSizes.thinBar))
                        .background(colors.surfaces.surfaceVariant),
            ) {
                val barColor = if (over) colors.semantics.error else colors.brands.primary
                Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(fraction).background(barColor))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = FintrackTheme.spacing.small),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Figure(label = stringResource(R.string.credit_limit_used), value = MoneyFormatter.format(state.outstanding))
                Figure(label = stringResource(R.string.credit_limit_left), value = availableText(state, over))
            }
        }
    }
}

// S-14: "Terpakai Rp4.220.000", the label muted and the figure bold
@Composable
private fun Figure(
    label: String,
    value: String,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall)) {
        Text(
            text = label,
            style = FintrackScreenText.rowSub(),
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(
            text = value,
            style = FintrackScreenText.number(size = 12f, weight = 700),
            color = FintrackTheme.colors.texts.onSurface,
        )
    }
}

@Composable
private fun availableText(
    state: CreditDetailUiState.Success,
    over: Boolean,
): String = if (over) stringResource(R.string.credit_limit_exceeded) else MoneyFormatter.format(state.availableLimit)
