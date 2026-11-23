package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.picker.DayGridDrawer
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
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
    val allLabel = stringResource(R.string.history_filter_all)
    when (state.sheet) {
        HistorySheet.MONTH -> {
            HistoryMonthPicker(state = state, onEvent = onEvent)
        }

        HistorySheet.WALLET -> {
            OptionsDrawer(
                title = stringResource(R.string.history_filter_wallet_title),
                options =
                    listOf(PickerOption(ALL_KEY, allLabel)) +
                        state.wallets.map { wallet ->
                            PickerOption(wallet.walletId.value.toString(), wallet.profile.name)
                        },
                selectedKey =
                    state.filter.walletId
                        ?.value
                        ?.toString() ?: ALL_KEY,
                onPick = { option ->
                    onEvent(HistoryEvent.WalletPicked(option.key.toLongOrNull()?.let { WalletId(it) }))
                },
                onClose = close,
            )
        }

        HistorySheet.CATEGORY -> {
            OptionsDrawer(
                title = stringResource(R.string.history_filter_category_title),
                options =
                    listOf(PickerOption(ALL_KEY, allLabel)) +
                        state.categories.map { category ->
                            PickerOption(category.categoryId.value.toString(), category.profile.name)
                        },
                selectedKey =
                    state.filter.categoryId
                        ?.value
                        ?.toString() ?: ALL_KEY,
                onPick = { option ->
                    onEvent(HistoryEvent.CategoryPicked(option.key.toLongOrNull()?.let { CategoryId(it) }))
                },
                onClose = close,
            )
        }

        HistorySheet.DAY -> {
            DayGridDrawer(
                title = stringResource(R.string.history_filter_day_title),
                selectedDay = state.filter.day,
                onPick = { day -> onEvent(HistoryEvent.DayPicked(day)) },
                onClose = close,
            )
        }

        null -> {
            Unit
        }
    }
}
