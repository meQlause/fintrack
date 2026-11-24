package com.meqlause.fintrack.feature.transaction.shortcut

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.PickerDrawer
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.core.ui.picker.OptionChoices
import com.meqlause.fintrack.core.ui.picker.OptionsCard
import com.meqlause.fintrack.core.ui.picker.PickerActions
import com.meqlause.fintrack.core.ui.picker.PickerMark
import com.meqlause.fintrack.core.ui.picker.WalletChoices
import com.meqlause.fintrack.core.ui.picker.WalletPickerDrawer
import com.meqlause.fintrack.core.ui.picker.WalletPickerRow
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.feature.transaction.edit.EditSheet
import com.meqlause.fintrack.feature.transaction.edit.pickerTitleRes
import com.meqlause.fintrack.feature.transaction.edit.toOption

// BR-006, FR-039: the categories of the open type, one card of option rows; a pick closes the drawer
@Composable
internal fun ShortcutCategoryDrawer(
    state: ShortcutFormUiState,
    onEvent: (ShortcutFormEvent) -> Unit,
) {
    PickerDrawer(
        title = stringResource(R.string.transaction_category_title),
        onClose = { onEvent(ShortcutFormEvent.SheetDismissed) },
    ) {
        OptionsCard(
            choices =
                OptionChoices(
                    options = state.categoryChoices.map { it.toOption() },
                    selectedKey =
                        state.draft.categoryId
                            ?.value
                            ?.toString(),
                ),
            onPick = { onEvent(ShortcutFormEvent.CategoryPicked(CategoryId(it.optionKey.toLong()))) },
        )
    }
}

// BR-039, FR-039: the wallets of the open type; an income shortcut is offered no credit wallet
@Composable
internal fun ShortcutWalletDrawer(
    state: ShortcutFormUiState,
    onEvent: (ShortcutFormEvent) -> Unit,
) {
    WalletPickerDrawer(
        title = stringResource(pickerTitleRes(state.draft.type, EditSheet.WALLET)),
        choices =
            WalletChoices(
                rows = walletRows(state),
                allowCredit = state.draft.type == TransactionType.EXPENSE,
            ),
        actions =
            PickerActions(
                onPick = { onEvent(ShortcutFormEvent.WalletPicked(it)) },
                onClose = { onEvent(ShortcutFormEvent.SheetDismissed) },
            ),
    )
}

// S-18: every active wallet with its compact balance, the picked one marked
private fun walletRows(state: ShortcutFormUiState): List<WalletPickerRow> =
    state.catalog.activeWallets().map { wallet ->
        WalletPickerRow(
            walletId = wallet.walletId,
            name = wallet.profile.name,
            type = wallet.kind.type,
            shownBalanceText = MoneyFormatter.formatShort(state.catalog.moneyByWallet[wallet.walletId] ?: Money(0L)),
            mark = PickerMark(isSelected = wallet.walletId == state.draft.walletId, isBlocked = false),
            appearance = wallet.profile.appearance,
        )
    }
