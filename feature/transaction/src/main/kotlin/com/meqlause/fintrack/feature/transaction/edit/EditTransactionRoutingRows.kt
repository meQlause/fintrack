package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.component.SheetPickRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackScreenSizes
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

// FR-031, FR-032, S-04: the route of the transaction: category tiles and wallet card, or both sides of a transfer
@Composable
internal fun EditTransactionRoutingRows(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (state.form.isTransfer) {
            EditTransferSides(
                state = state,
                onEvent = onEvent,
                modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall),
            )
            FormRow(
                label = stringResource(R.string.transaction_fee_label),
                value = MoneyFormatter.format(state.form.feeAmount),
                onClick = { onEvent(EditTransactionEvent.FieldFocused(KeypadField.FEE)) },
                modifier = Modifier.padding(top = FintrackTheme.spacing.small),
            )
        } else {
            EditCategoryTiles(
                state = state,
                onEvent = onEvent,
                modifier = Modifier.padding(top = FintrackScreenSizes.pillGap),
            )
            EditWalletCard(state = state, onEvent = onEvent)
            if (state.offersInstalment) {
                EditInstalmentRows(state = state, onEvent = onEvent)
            }
        }
    }
}

// S-04: "Bayar pakai · Debit" over the wallet name, its balance and a chevron; opens the wallet drawer
@Composable
private fun EditWalletCard(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
) {
    val info = EditWalletInfo.of(state, state.form.walletId)
    val base = stringResource(pickerTitleRes(state.form.type, EditSheet.WALLET))
    val group = info.groupRes?.let { stringResource(it) }
    SheetPickRow(
        label = if (group == null) base else stringResource(R.string.transaction_pay_group, base, group),
        name = info.name,
        balance = info.fundsText,
        iconKey = info.appearance.iconKey,
        colorKey = info.appearance.colorKey,
        onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.WALLET)) },
        modifier = Modifier.padding(top = FintrackTheme.spacing.medium),
    )
}

// S-04: the name shown on a wallet row, empty while no wallet is chosen
internal fun walletName(
    state: EditTransactionUiState,
    walletId: WalletId?,
): String = EditWalletInfo.of(state, walletId).name
