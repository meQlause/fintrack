package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.ui.picker.DayGridDrawer
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
import com.meqlause.fintrack.core.ui.picker.PickerOption
import com.meqlause.fintrack.feature.wallet.R
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
            OptionsDrawer(
                title = stringResource(R.string.wallet_picker_type_title),
                options =
                    WalletType.entries.map {
                        PickerOption(
                            optionKey = it.name,
                            label = stringResource(WalletLabels.type(it)),
                        )
                    },
                selectedKey = draft.type.name,
                onPick = { callbacks.onPickType(WalletType.valueOf(it.optionKey)) },
                onClose = callbacks.onDismissPicker,
            )
        }

        WalletPicker.INVEST_KIND -> {
            OptionsDrawer(
                title = stringResource(R.string.wallet_picker_invest_title),
                options =
                    InvestKind.entries.map {
                        PickerOption(
                            optionKey = it.name,
                            label = stringResource(WalletLabels.investKind(it)),
                        )
                    },
                selectedKey = draft.investKind.name,
                onPick = { callbacks.onPickInvestKind(InvestKind.valueOf(it.optionKey)) },
                onClose = callbacks.onDismissPicker,
            )
        }

        WalletPicker.STATEMENT_DAY -> {
            DayGridDrawer(
                title = stringResource(R.string.wallet_picker_statement_title),
                selectedDay = draft.statementDay,
                onPick = callbacks.onPickDay,
                onClose = callbacks.onDismissPicker,
            )
        }

        WalletPicker.DUE_DAY -> {
            DayGridDrawer(
                title = stringResource(R.string.wallet_picker_due_title),
                selectedDay = draft.dueDay,
                onPick = callbacks.onPickDay,
                onClose = callbacks.onDismissPicker,
            )
        }
    }
}
