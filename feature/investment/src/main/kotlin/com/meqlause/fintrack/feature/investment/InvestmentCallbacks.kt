package com.meqlause.fintrack.feature.investment

import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.WalletId

// S-08: every action the Investasi tab can take, wired to the ViewModel by InvestmentRoute
internal data class InvestmentCallbacks(
    val onAdd: () -> Unit,
    val onOpenAccount: (WalletId) -> Unit,
    val onNameChange: (String) -> Unit,
    val onOpenKindPicker: (Boolean) -> Unit,
    val onPickKind: (InvestKind) -> Unit,
    val sheet: AmountSheetCallbacks,
)
