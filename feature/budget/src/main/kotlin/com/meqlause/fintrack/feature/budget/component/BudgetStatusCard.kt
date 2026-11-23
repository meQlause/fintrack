package com.meqlause.fintrack.feature.budget.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.meqlause.fintrack.core.designsystem.component.ScreenCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenText
import com.meqlause.fintrack.core.designsystem.component.ProgressBar
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.budget.BudgetStatus
import com.meqlause.fintrack.core.model.BudgetState
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.budget.R

private const val PERCENT_SCALE = 100f

// FR-071, FR-072: one budget with its bar, spent of limit, and what is left or over; colour follows BR-020
@Composable
internal fun BudgetStatusCard(
    title: String,
    status: BudgetStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FintrackTheme.colors
    val isOver = status.spent > status.budget.limitAmount
    ScreenCard(modifier = modifier.clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(FintrackTheme.spacing.large)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = title,
                    style = FintrackScreenText.style(size = 13f, weight = 600),
                    color = colors.texts.onSurface,
                )
                Text(
                    text =
                        stringResource(
                            R.string.budget_used,
                            MoneyFormatter.format(status.spent),
                            MoneyFormatter.format(status.budget.limitAmount),
                        ),
                    style = FintrackScreenText.number(size = 13f, weight = 400),
                    color = colors.texts.onSurfaceVariant,
                )
            }
            ProgressBar(
                fraction = status.percent.toFloat() / PERCENT_SCALE,
                color = barColor(status.state),
                modifier = Modifier.padding(top = FintrackTheme.spacing.small),
            )
            Text(
                text = remainingText(status),
                style = FintrackScreenText.rowSub(),
                color = if (isOver) colors.semantics.error else colors.texts.onSurfaceVariant,
                modifier = Modifier.padding(top = FintrackTheme.spacing.small),
            )
        }
    }
}

// DOC-08, BR-020: the bar is primary below 80 percent, warning from 80, error from 100
@Composable
private fun barColor(state: BudgetState) =
    when (state) {
        BudgetState.NORMAL -> FintrackTheme.colors.brands.primary
        BudgetState.WARNING -> FintrackTheme.colors.semantics.warning
        BudgetState.EXCEEDED -> FintrackTheme.colors.semantics.error
    }

// FR-071: "Sisa" while under the limit, "Lebih" once spent passes it
@Composable
private fun remainingText(status: BudgetStatus): String {
    val limit = status.budget.limitAmount
    return if (status.spent > limit) {
        stringResource(R.string.budget_over, MoneyFormatter.format(status.spent - limit))
    } else {
        stringResource(R.string.budget_left, MoneyFormatter.format(limit - status.spent))
    }
}
