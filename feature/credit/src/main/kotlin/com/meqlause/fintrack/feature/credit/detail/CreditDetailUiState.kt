package com.meqlause.fintrack.feature.credit.detail

import com.meqlause.fintrack.core.domain.credit.StatementSummary
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.credit.instalment.InstalmentRow
import java.time.LocalDate

// FR-131, FR-138, S-14: what the credit wallet detail shows; Missing when the id is not a credit wallet
internal sealed interface CreditDetailUiState {
    data object Loading : CreditDetailUiState

    data object Missing : CreditDetailUiState

    data class Success(
        val name: String,
        val type: WalletType,
        val terms: CreditTerms,
        val statement: StatementSummary,
        val outstanding: Money,
        val availableLimit: Money,
        val today: LocalDate,
        val sources: List<PaySource>,
        val payDraft: PayDraft?,
        val picker: CreditDay?,
        val instalments: List<InstalmentRow> = emptyList(),
    ) : CreditDetailUiState
}
