package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.domain.credit.InstalmentRequest
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.usecase.SaveInstalmentPurchaseUseCase
import com.meqlause.fintrack.core.domain.usecase.SaveTransactionUseCase
import javax.inject.Inject

// FR-140, BR-038: stores the sheet as a plain transaction, or as a purchase with its instalment plan when Cicilan is on
internal class EditInstalmentController
    @Inject
    constructor(
        private val saveTransaction: SaveTransactionUseCase,
        private val saveInstalment: SaveInstalmentPurchaseUseCase,
    ) {
        /** FR-140: the plan is stored atomically with its purchase; a plain expense never creates a plan. */
        suspend fun store(state: EditTransactionUiState): SaveTransactionResult {
            val draft = state.toDraft()
            val tenor = state.instalment.tenor
            return if (state.instalmentActive && tenor != null) {
                saveInstalment(draft, InstalmentRequest(tenor, state.instalment.feeAmount))
            } else {
                saveTransaction(draft)
            }
        }

        // FR-140: the switch on the instalment block; turning it off keeps the typed tenor for next time
        fun toggled(
            state: EditTransactionUiState,
            enabled: Boolean,
        ): EditTransactionUiState =
            state.copy(instalment = state.instalment.copy(enabled = enabled), errors = emptySet())

        // FR-140: a months value fills the tenor; "Lainnya" clears it and focuses the keypad to type 2..36
        fun tenorPicked(
            state: EditTransactionUiState,
            months: Int?,
        ): EditTransactionUiState {
            val instalment =
                if (months == null) {
                    state.instalment.copy(tenorDigits = "")
                } else {
                    state.instalment.copy(tenorDigits = months.toString())
                }
            val keypad = if (months == null) KeypadField.TENOR else state.keypadField
            return state.copy(instalment = instalment, keypadField = keypad, openSheet = null, errors = emptySet())
        }
    }
