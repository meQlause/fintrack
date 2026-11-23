package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.picker.DayGridDrawer
import com.meqlause.fintrack.core.ui.picker.OptionChoices
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
import com.meqlause.fintrack.core.ui.picker.PickerActions
import com.meqlause.fintrack.core.ui.picker.PickerOption
import com.meqlause.fintrack.feature.transaction.R

private const val ALL_KEY = "all"

// FR-037, S-05: shows the one drawer the selection asks for; every choice closes it through the event
@Composable
internal fun HistorySheetHost(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    val close = { onEvent(HistoryEvent.SheetDismissed) }
    when (state.sheet) {
        HistorySheet.MONTH -> {
            HistoryMonthPicker(state = state, onEvent = onEvent)
        }

        HistorySheet.WALLET -> {
            WalletFilterDrawer(state = state, onEvent = onEvent, onClose = close)
        }

        HistorySheet.CATEGORY -> {
            CategoryFilterDrawer(state = state, onEvent = onEvent, onClose = close)
        }

        HistorySheet.DAY -> {
            DayGridDrawer(
                title = stringResource(R.string.history_filter_day_title),
                selectedDay = state.filter.dayOfMonth,
                actions = PickerActions(onPick = { day -> onEvent(HistoryEvent.DayPicked(day)) }, onClose = close),
            )
        }

        null -> {
            Unit
        }
    }
}

// FR-037: the wallets, with "Semua" on top; picking one filters the list
@Composable
private fun WalletFilterDrawer(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
    onClose: () -> Unit,
) {
    val everything = PickerOption(ALL_KEY, stringResource(R.string.history_filter_all))
    val options = listOf(everything) + state.wallets.map { PickerOption(it.walletId.value.toString(), it.profile.name) }
    OptionsDrawer(
        title = stringResource(R.string.history_filter_wallet_title),
        choices =
            OptionChoices(
                options = options,
                selectedKey =
                    state.filter.walletId
                        ?.value
                        ?.toString() ?: ALL_KEY,
            ),
        actions =
            PickerActions(
                onPick = { option ->
                    onEvent(HistoryEvent.WalletPicked(option.optionKey.toLongOrNull()?.let { WalletId(it) }))
                },
                onClose = onClose,
            ),
    )
}

// FR-037: the categories, with "Semua" on top; picking one filters the list
@Composable
private fun CategoryFilterDrawer(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
    onClose: () -> Unit,
) {
    val everything = PickerOption(ALL_KEY, stringResource(R.string.history_filter_all))
    val options =
        listOf(everything) + state.categories.map { PickerOption(it.categoryId.value.toString(), it.profile.name) }
    OptionsDrawer(
        title = stringResource(R.string.history_filter_category_title),
        choices =
            OptionChoices(
                options = options,
                selectedKey =
                    state.filter.categoryId
                        ?.value
                        ?.toString() ?: ALL_KEY,
            ),
        actions =
            PickerActions(
                onPick = { option ->
                    onEvent(HistoryEvent.CategoryPicked(option.optionKey.toLongOrNull()?.let { CategoryId(it) }))
                },
                onClose = onClose,
            ),
    )
}
