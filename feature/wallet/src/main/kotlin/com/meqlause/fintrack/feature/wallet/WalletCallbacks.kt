package com.meqlause.fintrack.feature.wallet

import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletType

// FR-010..016: every user action of S-09, passed to the stateless screen as plain lambdas
internal data class WalletCallbacks(
    val onAdd: () -> Unit = {},
    val onOpenDetail: (WalletId) -> Unit = {},
    val onEdit: (WalletItem) -> Unit = {},
    val onToggleArchived: () -> Unit = {},
    val onNameChange: (String) -> Unit = {},
    val onOpenPicker: (WalletPicker) -> Unit = {},
    val onDismissPicker: () -> Unit = {},
    val onPickType: (WalletType) -> Unit = {},
    val onPickInvestKind: (InvestKind) -> Unit = {},
    val onPickDay: (Int) -> Unit = {},
    val onInitialBalanceChange: (String) -> Unit = {},
    val onCreditLimitChange: (String) -> Unit = {},
    val onIncludeInTotalChange: (Boolean) -> Unit = {},
    val onSetArchivedEditing: (Boolean) -> Unit = {},
    val onSave: () -> Unit = {},
    val onDismissEditor: () -> Unit = {},
)
