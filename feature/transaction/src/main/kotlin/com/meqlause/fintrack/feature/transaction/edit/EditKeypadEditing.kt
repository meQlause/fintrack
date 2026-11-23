package com.meqlause.fintrack.feature.transaction.edit

// FR-030, FR-140: the digits typed on the keypad go to whichever field is focused, the amount or an instalment field
internal fun EditTransactionUiState.withEditedField(transform: (String) -> String): EditTransactionUiState {
    val edited =
        when (keypadField) {
            KeypadField.AMOUNT -> {
                copy(form = form.copy(rupiahDigits = transform(form.rupiahDigits)))
            }

            KeypadField.FEE -> {
                copy(form = form.copy(feeDigits = transform(form.feeDigits)))
            }

            KeypadField.TENOR -> {
                withExtras { copy(instalment = instalment.copy(tenorDigits = transform(instalment.tenorDigits))) }
            }

            KeypadField.INSTALMENT_FEE -> {
                withExtras { copy(instalment = instalment.copy(feeDigits = transform(instalment.feeDigits))) }
            }
        }
    return edited.withStatus { copy(errors = emptySet()) }
}
