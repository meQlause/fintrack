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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.meqlause.fintrack.core.designsystem.component.FintrackCard
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.ui.format.DateFormatter
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
            CategoryTotal(state = state, modifier = Modifier.weight(1f))
            OpenChevron()
        }
        CategoryBar(categories = state.categories, modifier = Modifier.padding(top = FintrackTheme.spacing.stack))
        CategoryLegend(
            categories = state.categories.take(TOP_COUNT),
            total = expense,
            modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
        )
    }
}

// FR-042: the month caption over the period expense total, masked when amounts are hidden
@Composable
private fun CategoryTotal(
    state: HomeUiState.Success,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text =
                stringResource(
                    R.string.home_category_title,
                    DateFormatter.monthShort(state.period.startDate),
                ),
            style = FintrackTheme.typography.caption,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        HomeAmountText(
            money = state.period.expense,
            hidden = state.hidden,
            style = FintrackTheme.typography.numberTotal,
            modifier = Modifier.padding(top = FintrackTheme.spacing.xxSmall),
        )
    }
}

// S-03: the round chevron that signals the card opens the Kategori drawer
@Composable
private fun OpenChevron() {
    Box(
        modifier =
            Modifier
                .size(FintrackTheme.sizes.iconTile)
                .clip(FintrackTheme.shapes.full)
                .background(FintrackTheme.colors.surfaces.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(DesignR.drawable.ic_chevron_right),
            contentDescription = stringResource(R.string.home_category_open),
            colorFilter = ColorFilter.tint(FintrackTheme.colors.texts.onSurface),
            modifier = Modifier.size(FintrackTheme.sizes.shortcutGlyph),
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
                .height(FintrackTheme.sizes.categoryBarHeight)
                .clip(FintrackTheme.shapes.small),
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xxSmall),
    ) {
        categories.forEach { share ->
            Box(
                modifier =
                    Modifier
                        .weight(share.amount.rupiah.toFloat())
                        .fillMaxHeight()
                        .background(FintrackTheme.colors.categoryColor(share.colorKey)),
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
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.stack)) {
        categories.forEach { share ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(FintrackTheme.spacing.xSmall + FintrackTheme.spacing.xxSmall),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(FintrackTheme.sizes.smallDot)
                            .clip(FintrackTheme.shapes.full)
                            .background(FintrackTheme.colors.categoryColor(share.colorKey)),
                )
                Text(
                    text = legendText(share = share, total = total),
                    style = FintrackTheme.typography.captionWide.copy(fontWeight = FontWeight.Medium),
                    color = FintrackTheme.colors.texts.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// DOC-08: the name in the muted weight, then the percentage in bold
@Composable
private fun legendText(
    share: HomeCategoryShare,
    total: Money,
) = buildAnnotatedString {
    append(share.name ?: stringResource(R.string.home_category_none))
    append(" ")
    val percent = HomeCalculator.percentOf(share.amount, total)
    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = FintrackTheme.colors.texts.onSurface)) {
        append(stringResource(R.string.home_percent, percent))
    }
}

private const val TOP_COUNT = 3
