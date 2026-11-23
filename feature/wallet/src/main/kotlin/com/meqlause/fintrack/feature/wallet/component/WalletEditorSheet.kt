package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.FintrackButton
import com.meqlause.fintrack.core.designsystem.component.FintrackSwitch
import com.meqlause.fintrack.core.designsystem.component.FormRow
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.feature.wallet.WalletCallbacks
import com.meqlause.fintrack.feature.wallet.WalletEditor
import com.meqlause.fintrack.feature.wallet.WalletLabels
import com.meqlause.fintrack.feature.wallet.WalletPicker

// FR-010, FR-011, FR-139, FR-170, S-09: the create and edit sheet; every choice opens a drawer
@Composable
internal fun WalletEditorSheet(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    FintrackBottomSheet {
        Text(
            text =
                stringResource(
                    if (draft.walletId ==
                        null
                    ) {
                        R.string.wallet_editor_new
                    } else {
                        R.string.wallet_editor_edit
                    },
                ),
            style = FintrackTheme.typography.titleMedium,
            color = FintrackTheme.colors.texts.onSurface,
        )
        WalletNameField(draft = draft, onNameChange = callbacks.onNameChange)
        WalletFormFields(draft = draft, callbacks = callbacks)
        WalletEditorActions(draft = draft, callbacks = callbacks)
    }
}

// FR-010: the name, with its rejection reason under the field (FR-002 rules)
@Composable
private fun WalletNameField(
    draft: WalletEditor,
    onNameChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = draft.name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.wallet_field_name)) },
        singleLine = true,
        isError = draft.nameError != null,
    )
    draft.nameError?.let { reason ->
        ErrorText(text = stringResource(nameErrorRes(reason)))
    }
}

// FR-010, FR-139, FR-170: type, investment kind, opening balance, credit terms and the total switch
@Composable
private fun WalletFormFields(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FormRow(
            label = stringResource(R.string.wallet_field_type),
            value = stringResource(WalletLabels.type(draft.type)),
            onClick = { callbacks.onOpenPicker(WalletPicker.TYPE) },
        )
        if (draft.isInvestment) {
            FormRow(
                label = stringResource(R.string.wallet_field_invest_kind),
                value = stringResource(WalletLabels.investKind(draft.investKind)),
                onClick = { callbacks.onOpenPicker(WalletPicker.INVEST_KIND) },
            )
        }
        BalanceField(draft = draft, onChange = callbacks.onInitialBalanceChange)
        if (draft.isCredit) {
            CreditFields(draft = draft, callbacks = callbacks)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.wallet_include_total),
                style = FintrackTheme.typography.bodyLarge,
                color = FintrackTheme.colors.texts.onSurface,
            )
            FintrackSwitch(checked = draft.includeInTotal, onCheckedChange = callbacks.onIncludeInTotalChange)
        }
    }
}

// FR-010, FR-170: the label follows the wallet kind (Saldo awal, Modal awal or Utang saat ini)
@Composable
private fun BalanceField(
    draft: WalletEditor,
    onChange: (String) -> Unit,
) {
    val labelRes =
        when {
            draft.isCredit -> R.string.wallet_field_debt
            draft.isInvestment -> R.string.wallet_field_invested
            else -> R.string.wallet_field_initial_balance
        }
    OutlinedTextField(
        value = draft.initialBalance,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(labelRes)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}

// FR-139: limit, statement day and due day of a card or paylater wallet
@Composable
private fun CreditFields(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        OutlinedTextField(
            value = draft.creditLimit,
            onValueChange = callbacks.onCreditLimitChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(R.string.wallet_field_limit)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = draft.limitError,
        )
        if (draft.limitError) {
            ErrorText(text = stringResource(R.string.wallet_limit_invalid))
        }
        FormRow(
            label = stringResource(R.string.wallet_field_statement_day),
            value = stringResource(R.string.wallet_day_value, draft.statementDay),
            onClick = { callbacks.onOpenPicker(WalletPicker.STATEMENT_DAY) },
        )
        FormRow(
            label = stringResource(R.string.wallet_field_due_day),
            value = stringResource(R.string.wallet_day_value, draft.dueDay),
            onClick = { callbacks.onOpenPicker(WalletPicker.DUE_DAY) },
        )
    }
}

// FR-010, FR-012: save, archive or restore (edits only), and cancel
@Composable
private fun WalletEditorActions(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small)) {
        FintrackButton(text = stringResource(R.string.wallet_save), onClick = callbacks.onSave)
        if (draft.walletId != null) {
            val archiveRes = if (draft.isArchived) R.string.wallet_restore else R.string.wallet_archive
            FintrackButton(
                text = stringResource(archiveRes),
                onClick = { callbacks.onSetArchivedEditing(!draft.isArchived) },
            )
        }
        FintrackButton(text = stringResource(R.string.wallet_cancel), onClick = callbacks.onDismissEditor)
    }
}

@Composable
private fun ErrorText(text: String) {
    Text(text = text, style = FintrackTheme.typography.bodyMedium, color = FintrackTheme.colors.semantics.error)
}

private fun nameErrorRes(reason: WalletNameError): Int =
    when (reason) {
        WalletNameError.BLANK -> R.string.wallet_name_blank
        WalletNameError.TOO_LONG -> R.string.wallet_name_too_long
    }
