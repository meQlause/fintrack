package com.meqlause.fintrack.feature.transaction.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.meqlause.fintrack.core.designsystem.component.FintrackChip
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.ui.format.MoneyFormatter
import com.meqlause.fintrack.feature.transaction.R

// FR-031, FR-032, S-04: the route of the transaction — category and wallet for EXPENSE and INCOME, source and target for TRANSFER
@Composable
internal fun EditTransactionRoutingRows(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        if (state.form.isTransfer) {
            TransferRoute(state = state, onEvent = onEvent)
            FormRow(
                label = stringResource(R.string.transaction_fee_label),
                value = MoneyFormatter.format(state.form.fee),
                onClick = { onEvent(EditTransactionEvent.FieldFocused(KeypadField.FEE)) },
            )
        } else {
            CategoryChips(state = state, onEvent = onEvent)
            FormRow(
                label = stringResource(pickerTitleRes(state.form.type, EditSheet.WALLET)),
                value = walletName(state, state.form.walletId),
                onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.WALLET)) },
            )
        }
    }
}

// S-04: Dari ⇄ Ke laid out left to right; the swap button exchanges both sides
@Composable
private fun TransferRoute(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
) {
    val swapLabel = stringResource(R.string.transaction_swap_description)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
    ) {
        WalletCard(
            label = stringResource(R.string.transaction_transfer_from),
            name = walletName(state, state.form.walletId),
            onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.WALLET)) },
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "⇄",
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.semantics.transfer,
            modifier =
                Modifier
                    .clickable { onEvent(EditTransactionEvent.WalletsSwapped) }
                    .semantics { contentDescription = swapLabel },
        )
        WalletCard(
            label = stringResource(R.string.transaction_transfer_to),
            name = walletName(state, state.form.targetWalletId),
            onClick = { onEvent(EditTransactionEvent.SheetOpened(EditSheet.TARGET_WALLET)) },
            modifier = Modifier.weight(1f),
        )
    }
}

// S-04: one side of the transfer; tapping it opens the wallet drawer for that side
@Composable
private fun WalletCard(
    label: String,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(FintrackTheme.shapes.medium)
                .background(FintrackTheme.colors.surfaces.surface)
                .clickable(onClick = onClick)
                .padding(FintrackTheme.spacing.medium),
    ) {
        Text(
            text = label,
            style = FintrackTheme.typography.labelMedium,
            color = FintrackTheme.colors.texts.onSurfaceVariant,
        )
        Text(text = name, style = FintrackTheme.typography.titleMedium, color = FintrackTheme.colors.texts.onSurface)
    }
}

// FR-031, S-04: horizontal scroll of the categories of the current kind; the selected one is highlighted
@Composable
private fun CategoryChips(
    state: EditTransactionUiState,
    onEvent: (EditTransactionEvent) -> Unit,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        items(items = state.categoryChoices, key = { category -> category.categoryId.value }) { category ->
            FintrackChip(
                label = category.profile.name,
                selected = category.categoryId == state.form.categoryId,
                onClick = { onEvent(EditTransactionEvent.CategoryPicked(category.categoryId)) },
            )
        }
    }
}

// S-04: the name shown on a wallet row, empty while no wallet is chosen
internal fun walletName(
    state: EditTransactionUiState,
    walletId: WalletId?,
): String =
    state.catalog.wallets
        .firstOrNull { it.walletId == walletId }
        ?.profile
        ?.name
        .orEmpty()
