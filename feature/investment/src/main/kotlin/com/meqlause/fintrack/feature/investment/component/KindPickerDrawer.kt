package com.meqlause.fintrack.feature.investment.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.feature.investment.InvestmentCallbacks
import com.meqlause.fintrack.feature.investment.InvestmentLabels
import com.meqlause.fintrack.feature.investment.R

// FR-170: the drawer that picks the kind of a new investment; one tap picks and closes it
@Composable
internal fun KindPickerDrawer(
    selected: InvestKind?,
    callbacks: InvestmentCallbacks,
) {
    PickerDrawer(
        title = stringResource(R.string.investment_kind_drawer_title),
        onClose = { callbacks.onOpenKindPicker(false) },
    ) {
        Column {
            InvestKind.entries.forEach { kind ->
                KindOption(kind = kind, isSelected = kind == selected, onClick = { callbacks.onPickKind(kind) })
            }
        }
    }
}

// FR-170: one kind in the drawer, with the chosen one marked
@Composable
private fun KindOption(
    kind: InvestKind,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val typography = FintrackTheme.typography
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = FintrackTheme.sizes.touchTarget)
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(InvestmentLabels.kindRes(kind)),
            modifier = Modifier.weight(1f),
            style = typography.bodyLarge,
            color = FintrackTheme.colors.texts.onSurface,
        )
        if (isSelected) {
            Text(
                text = stringResource(R.string.investment_selected),
                style = typography.labelMedium,
                color = FintrackTheme.colors.brands.primary,
            )
        }
    }
}
