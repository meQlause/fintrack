package com.meqlause.fintrack.feature.inbox

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.picker.OptionChoices
import com.meqlause.fintrack.core.ui.picker.OptionsDrawer
import com.meqlause.fintrack.core.ui.picker.PickerActions
import com.meqlause.fintrack.core.ui.picker.PickerOption

// S-07: which chip is open; the id is the inbox item whose wallet or category changes
internal data class InboxPicker(
    val itemId: Long,
    val kind: PickKind,
)

internal enum class PickKind {
    WALLET,
    TARGET_WALLET,
    CATEGORY,
}

// S-07, FR-087: one drawer for the wallet or category chip; the choice is applied to the card or pair side
@Composable
internal fun InboxPickerDrawer(
    picker: InboxPicker,
    state: InboxUiState,
    actions: InboxActions,
    onClose: () -> Unit,
) {
    val options = optionsFor(picker, state)
    val selected = selectedKey(picker, state)
    OptionsDrawer(
        title = titleFor(picker.kind),
        choices = OptionChoices(options = options, selectedKey = selected),
        actions =
            PickerActions(onPick = { option ->
                onPicked(picker, option, actions.items)
                onClose()
            }, onClose = onClose),
    )
}

@Composable
private fun titleFor(kind: PickKind): String =
    when (kind) {
        PickKind.WALLET -> stringResource(R.string.inbox_picker_wallet)
        PickKind.TARGET_WALLET -> stringResource(R.string.inbox_picker_target)
        PickKind.CATEGORY -> stringResource(R.string.inbox_picker_category)
    }

private fun optionsFor(
    picker: InboxPicker,
    state: InboxUiState,
): List<PickerOption> =
    when (picker.kind) {
        PickKind.WALLET, PickKind.TARGET_WALLET -> state.options.wallets
        PickKind.CATEGORY -> categoryOptions(picker.itemId, state)
    }

// S-18: an income card lists the income categories, every other card the expense categories
private fun categoryOptions(
    itemId: Long,
    state: InboxUiState,
): List<PickerOption> =
    if (typeOf(itemId, state) == TransactionType.INCOME) state.options.incomes else state.options.expenses

private fun selectedKey(
    picker: InboxPicker,
    state: InboxUiState,
): String? {
    val card = state.pending.cards.firstOrNull { it.itemId == picker.itemId }
    val pair = state.pending.pairs.firstOrNull { it.fromItemId == picker.itemId || it.toItemId == picker.itemId }
    return when (picker.kind) {
        PickKind.CATEGORY -> card?.category?.slotId?.toString()
        PickKind.WALLET -> card?.wallet?.slotId?.toString() ?: pair?.from?.slotId?.toString()
        PickKind.TARGET_WALLET -> pair?.target?.slotId?.toString()
    }
}

private fun typeOf(
    itemId: Long,
    state: InboxUiState,
): TransactionType? =
    state.pending.cards
        .firstOrNull { it.itemId == itemId }
        ?.type

private fun onPicked(
    picker: InboxPicker,
    option: PickerOption,
    actions: InboxItemActions,
) {
    val optionId = option.optionKey.toLong()
    when (picker.kind) {
        PickKind.CATEGORY -> actions.onChooseCategory(picker.itemId, optionId)
        PickKind.WALLET, PickKind.TARGET_WALLET -> actions.onChooseWallet(picker.itemId, optionId)
    }
}
