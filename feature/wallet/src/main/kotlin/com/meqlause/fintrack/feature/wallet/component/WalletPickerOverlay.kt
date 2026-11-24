package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.ui.picker.DayGridDrawer
import com.meqlause.fintrack.core.ui.picker.OptionChoices
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
import com.meqlause.fintrack.core.ui.picker.PickerActions
import com.meqlause.fintrack.core.ui.picker.PickerOption
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.feature.wallet.WalletAppearance
import com.meqlause.fintrack.feature.wallet.WalletCallbacks
import com.meqlause.fintrack.feature.wallet.WalletEditor
import com.meqlause.fintrack.feature.wallet.WalletLabels
import com.meqlause.fintrack.feature.wallet.WalletPicker

// S-18, FR-139, FR-170: the drawer the editor has open; type and investment kind are options, days are a grid
@Composable
internal fun WalletPickerOverlay(
    picker: WalletPicker,
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    when (picker) {
        WalletPicker.TYPE -> {
            TypeDrawer(draft = draft, callbacks = callbacks)
        }

        WalletPicker.INVEST_KIND -> {
            InvestKindDrawer(draft = draft, callbacks = callbacks)
        }

        WalletPicker.STATEMENT_DAY -> {
            DayGridDrawer(
                title = stringResource(R.string.wallet_picker_statement_title),
                selectedDay = draft.statementDay,
                actions = PickerActions(onPick = callbacks.onPickDay, onClose = callbacks.onDismissPicker),
            )
        }

        WalletPicker.DUE_DAY -> {
            DayGridDrawer(
                title = stringResource(R.string.wallet_picker_due_title),
                selectedDay = draft.dueDay,
                actions = PickerActions(onPick = callbacks.onPickDay, onClose = callbacks.onDismissPicker),
            )
        }
    }
}

// FR-010: the wallet type as a single-choice list
@Composable
private fun TypeDrawer(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    val options = WalletType.entries.map { type -> typeOption(type) }
    OptionsDrawer(
        title = stringResource(R.string.wallet_picker_type_title),
        choices = OptionChoices(options = options, selectedKey = draft.type.name),
        actions =
            PickerActions(
                onPick = { callbacks.onPickType(WalletType.valueOf(it.optionKey)) },
                onClose = callbacks.onDismissPicker,
            ),
    )
}

// FR-010: a wallet type with its tinted icon and its grey description
@Composable
private fun typeOption(type: WalletType): PickerOption {
    val appearance = WalletAppearance.forType(type)
    return PickerOption(
        optionKey = type.name,
        label = stringResource(WalletLabels.type(type)),
        hint = WalletLabels.typeHint(type)?.let { stringResource(it) },
        look = IconLook(appearance.iconKey, appearance.colorKey),
    )
}

// FR-170: the investment kind as a single-choice list
@Composable
private fun InvestKindDrawer(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    val options = InvestKind.entries.map { PickerOption(it.name, stringResource(WalletLabels.investKind(it))) }
    OptionsDrawer(
        title = stringResource(R.string.wallet_picker_invest_title),
        choices = OptionChoices(options = options, selectedKey = draft.investKind.name),
        actions =
            PickerActions(
                onPick = { callbacks.onPickInvestKind(InvestKind.valueOf(it.optionKey)) },
                onClose = callbacks.onDismissPicker,
            ),
    )
}
