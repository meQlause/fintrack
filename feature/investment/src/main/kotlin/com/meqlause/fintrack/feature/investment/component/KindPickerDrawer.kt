package com.meqlause.fintrack.feature.investment.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.ui.picker.OptionChoices
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
import com.meqlause.fintrack.core.ui.picker.PickerActions
import com.meqlause.fintrack.core.ui.picker.PickerOption
import com.meqlause.fintrack.feature.investment.InvestmentCallbacks
import com.meqlause.fintrack.feature.investment.InvestmentLabels
import com.meqlause.fintrack.feature.investment.R

// FR-170, S-18: the drawer that picks the kind of a new investment, the shared options card; a tap picks and closes
@Composable
internal fun KindPickerDrawer(
    selected: InvestKind?,
    callbacks: InvestmentCallbacks,
) {
    val options =
        InvestKind.entries.map { kind ->
            PickerOption(optionKey = kind.name, label = stringResource(InvestmentLabels.kindRes(kind)))
        }
    OptionsDrawer(
        title = stringResource(R.string.investment_kind_drawer_title),
        choices = OptionChoices(options = options, selectedKey = selected?.name),
        actions =
            PickerActions(
                onPick = { option -> callbacks.onPickKind(InvestKind.valueOf(option.optionKey)) },
                onClose = { callbacks.onOpenKindPicker(false) },
            ),
    )
}
