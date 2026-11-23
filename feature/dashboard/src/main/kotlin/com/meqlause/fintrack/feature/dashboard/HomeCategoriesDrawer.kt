package com.meqlause.fintrack.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.Money

// FR-042, S-03: the Kategori drawer: every expense category with its share, count and amount
@Composable
internal fun HomeCategoriesDrawer(
    state: HomeUiState.Success,
    onClose: () -> Unit,
) {
    PickerDrawer(title = stringResource(R.string.home_categories_title), onClose = onClose) {
        state.categories.forEachIndexed { index, share ->
            CategoryLine(
                name = share.name ?: stringResource(R.string.home_category_none),
                count = share.count,
                amount = share.amount,
                percent = HomeCalculator.percentOf(share.amount, state.period.expense),
                hidden = state.hidden,
                color = homeSegmentColor(FintrackTheme.colors, index),
            )
        }
    }
}

// S-03: one category line: colour dot, name with count, then amount and percent
@Composable
private fun CategoryLine(
    name: String,
    count: Int,
    amount: Money,
    percent: Int,
    hidden: Boolean,
    color: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = FintrackTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.medium),
    ) {
        Box(modifier = Modifier.size(FintrackTheme.spacing.small).clip(FintrackTheme.shapes.full).background(color))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = FintrackTheme.typography.bodyLarge,
                color = FintrackTheme.colors.texts.onSurface,
            )
            Text(
                text = pluralStringResource(R.plurals.home_category_count, count, count),
                style = FintrackTheme.typography.labelMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.xxSmall),
        ) {
            HomeAmountText(money = amount, hidden = hidden, style = FintrackTheme.typography.bodyLarge)
            Text(
                text = stringResource(R.string.home_percent, percent),
                style = FintrackTheme.typography.labelMedium,
                color = FintrackTheme.colors.texts.onSurfaceVariant,
            )
        }
    }
}
