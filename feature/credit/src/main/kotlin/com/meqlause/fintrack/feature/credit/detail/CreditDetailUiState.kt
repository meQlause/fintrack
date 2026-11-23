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
        val identity: CreditIdentity,
        val figures: CreditFigures,
        val today: LocalDate,
        val payment: PayState,
        val picker: CreditDay?,
        val instalments: List<InstalmentRow> = emptyList(),
    ) : CreditDetailUiState {
        val name: String get() = identity.name
        val type: WalletType get() = identity.type
        val terms: CreditTerms get() = figures.terms
        val statement: StatementSummary get() = figures.statement
        val outstanding: Money get() = figures.outstanding
        val availableLimit: Money get() = figures.availableLimit
        val sources: List<PaySource> get() = payment.sources
        val payDraft: PayDraft? get() = payment.payDraft
    }
}

// S-14: the wallet name and whether it is a card or a paylater
internal data class CreditIdentity(
    val name: String,
    val type: WalletType,
)

// S-14: the terms, the bill and the two money figures the limit bar shows
internal data class CreditFigures(
    val terms: CreditTerms,
    val statement: StatementSummary,
    val outstanding: Money,
    val availableLimit: Money,
)

// S-15: the wallets the bill can be paid from and the draft of the payment in progress
internal data class PayState(
    val sources: List<PaySource>,
    val payDraft: PayDraft?,
)
