package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-042, S-03: the expense card: period total, one slim stacked bar and the top three categories; tap opens Kategori
@Composable
internal fun HomeCategoryCard(
    state: HomeUiState.Success,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val expense = state.period.expense
    FintrackCard(modifier = modifier.fillMaxWidth(), onClick = onOpen) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_category_title),
                    style = FintrackTheme.typography.labelLarge,
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                )
                HomeAmountText(money = expense, hidden = state.hidden, style = FintrackTheme.typography.numberLarge)
            }
            Image(
                painter = painterResource(DesignR.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.home_category_open),
                colorFilter = ColorFilter.tint(FintrackTheme.colors.texts.onSurfaceVariant),
                modifier = Modifier.size(FintrackTheme.sizes.iconSmall),
            )
        }
        CategoryBar(categories = state.categories, modifier = Modifier.padding(top = FintrackTheme.spacing.medium))
        CategoryLegend(
            categories = state.categories.take(TOP_COUNT),
            total = expense,
            modifier = Modifier.padding(top = FintrackTheme.spacing.small),
        )
    }
}

// FR-042: every expense category as one stacked bar, in the same order as the legend
@Composable
private fun CategoryBar(
    categories: List<HomeCategoryShare>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(FintrackTheme.spacing.xSmall)
                .clip(FintrackTheme.shapes.full),
    ) {
        categories.forEachIndexed { index, share ->
            Box(
                modifier =
                    Modifier
                        .weight(share.amount.rupiah.toFloat())
                        .fillMaxHeight()
                        .background(homeSegmentColor(FintrackTheme.colors, index)),
            )
        }
    }
}

// FR-042: the top three categories with their share of the period expense
@Composable
private fun CategoryLegend(
    categories: List<HomeCategoryShare>,
    total: Money,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        categories.forEachIndexed { index, share ->
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xSmall),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(FintrackTheme.spacing.small)
                            .clip(FintrackTheme.shapes.full)
                            .background(homeSegmentColor(FintrackTheme.colors, index)),
                )
                Text(
                    text = legendText(share = share, total = total),
                    style = FintrackTheme.typography.labelMedium,
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun legendText(
    share: HomeCategoryShare,
    total: Money,
): String {
    val name = share.name ?: stringResource(R.string.home_category_none)
    val percent = HomeCalculator.percentOf(share.amount, total)
    return stringResource(R.string.home_category_legend, name, percent)
}

private const val TOP_COUNT = 3
