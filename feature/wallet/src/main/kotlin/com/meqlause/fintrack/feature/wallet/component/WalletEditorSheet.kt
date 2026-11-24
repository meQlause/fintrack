package com.meqlause.fintrack.feature.wallet.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.component.FintrackBottomSheet
import com.meqlause.fintrack.core.designsystem.component.ScreenDashedButton
import com.meqlause.fintrack.core.designsystem.component.ScreenPrimaryButton
import com.meqlause.fintrack.core.designsystem.component.SheetFieldLabel
import com.meqlause.fintrack.core.designsystem.component.SheetHeader
import com.meqlause.fintrack.core.designsystem.component.SheetNumberField
import com.meqlause.fintrack.core.designsystem.component.SheetTextField
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.feature.wallet.R
import com.meqlause.fintrack.feature.wallet.WalletCallbacks
import com.meqlause.fintrack.feature.wallet.WalletEditor

// FR-010, FR-011, FR-139, FR-170, S-09: the create and edit sheet; every choice replaces it with a picker
@Composable
internal fun WalletEditorSheet(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    FintrackBottomSheet(onDismiss = callbacks.onDismissEditor) {
        val title = if (draft.walletId == null) R.string.wallet_editor_new else R.string.wallet_editor_edit
        SheetHeader(title = stringResource(title), onClose = callbacks.onDismissEditor)
        WalletNameField(draft = draft, onNameChange = callbacks.onNameChange)
        WalletFormFields(draft = draft, callbacks = callbacks)
        Column(
            modifier = Modifier.padding(top = FintrackTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(FintrackTheme.spacing.small),
        ) {
            ScreenPrimaryButton(text = stringResource(R.string.wallet_save), onClick = callbacks.onSave)
            ArchiveButton(draft = draft, callbacks = callbacks)
        }
    }
}

// FR-010: the name, with its rejection reason under the field (FR-002 rules)
@Composable
private fun WalletNameField(
    draft: WalletEditor,
    onNameChange: (String) -> Unit,
) {
    SheetFieldLabel(text = stringResource(R.string.wallet_field_name))
    SheetTextField(
        value = draft.name,
        onValueChange = onNameChange,
        placeholder = stringResource(R.string.wallet_name_hint),
    )
    draft.nameError?.let { reason ->
        ErrorText(text = stringResource(nameErrorRes(reason)))
    }
}

// FR-012: archive or restore, offered when an existing wallet is edited
@Composable
private fun ArchiveButton(
    draft: WalletEditor,
    callbacks: WalletCallbacks,
) {
    if (draft.walletId == null) return
    val archiveRes = if (draft.isArchived) R.string.wallet_restore else R.string.wallet_archive
    ScreenDashedButton(
        text = stringResource(archiveRes),
        onClick = { callbacks.onSetArchivedEditing(!draft.isArchived) },
    )
}

// FR-010, FR-170: the label follows the wallet kind (Saldo awal, Modal awal or Utang saat ini)
@Composable
internal fun BalanceField(
    draft: WalletEditor,
    onChange: (String) -> Unit,
) {
    val labelRes =
        when {
            draft.isCredit -> R.string.wallet_field_debt
            draft.isInvestment -> R.string.wallet_field_invested
            else -> R.string.wallet_field_initial_balance
        }
    SheetFieldLabel(text = stringResource(labelRes))
    SheetNumberField(
        value = draft.initialBalance,
        onValueChange = onChange,
        placeholder = stringResource(R.string.wallet_balance_hint),
    )
}

@Composable
internal fun ErrorText(text: String) {
    Text(
        text = text,
        style = FintrackTheme.typography.bodyMedium,
        color = FintrackTheme.colors.semantics.error,
        modifier = Modifier.padding(top = FintrackTheme.spacing.xSmall),
    )
}

private fun nameErrorRes(reason: WalletNameError): Int =
    when (reason) {
        WalletNameError.BLANK -> R.string.wallet_name_blank
        WalletNameError.TOO_LONG -> R.string.wallet_name_too_long
    }
