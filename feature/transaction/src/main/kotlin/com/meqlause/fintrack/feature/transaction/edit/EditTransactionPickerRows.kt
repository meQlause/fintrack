package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.core.ui.picker.WalletPickerDrawer
import com.meqlause.fintrack.core.ui.picker.WalletPickerRow
import com.meqlause.fintrack.feature.transaction.R

// FR-032, S-18: the wallet drawer of the sheet; credit wallets are allowed everywhere except as a transfer source (BR-039)
@Composable
internal fun EditTransactionWalletPicker(
    state: EditTransactionUiState,
    sheet: EditSheet,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    WalletPickerDrawer(
        title = stringResource(pickerTitleRes(state.form.type, sheet)),
        rows = pickerRows(state, sheet),
        onPick = { walletId -> onEvent(EditTransactionEvent.WalletPicked(walletId)) },
        onClose = { onEvent(EditTransactionEvent.SheetDismissed) },
        modifier = modifier,
        allowCredit = sheet == EditSheet.TARGET_WALLET || state.form.type == TransactionType.EXPENSE,
    )
}

// S-04: the drawer title and the row label share one wording per type and side
internal fun pickerTitleRes(
    type: TransactionType,
    sheet: EditSheet,
): Int =
    when {
        sheet == EditSheet.TARGET_WALLET -> R.string.transaction_transfer_to
        type == TransactionType.TRANSFER -> R.string.transaction_transfer_from
        type == TransactionType.INCOME -> R.string.transaction_income_into
        else -> R.string.transaction_pay_with
    }

// S-18: every active wallet, with the other side of a transfer marked "Sudah dipilih"
internal fun pickerRows(
    state: EditTransactionUiState,
    sheet: EditSheet,
): List<WalletPickerRow> {
    val form = state.form
    val isTarget = sheet == EditSheet.TARGET_WALLET
    val selected = if (isTarget) form.targetWalletId else form.walletId
    val blocked = if (isTarget) form.walletId else form.targetWalletId.takeIf { form.isTransfer }
    return state.catalog.activeWallets().map { wallet ->
        WalletPickerRow(
            walletId = wallet.walletId,
            name = wallet.profile.name,
            type = wallet.kind.type,
            shownBalanceText = MoneyFormatter.format(state.catalog.moneyByWallet[wallet.walletId] ?: Money(0L)),
            isSelected = wallet.walletId == selected,
            isBlocked = wallet.walletId == blocked,
        )
    }
}
