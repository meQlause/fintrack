package com.meqlause.fintrack.feature.credit.detail

import androidx.annotation.StringRes
import com.meqlause.fintrack.core.model.StatementStatus
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.feature.credit.R

// FR-131, FR-139: Indonesian labels of the credit wallet types and of the statement status (BR-034)
internal object CreditLabels {
    @StringRes
    fun type(type: WalletType): Int =
        if (type == WalletType.PAYLATER) R.string.credit_type_paylater else R.string.credit_type_card

    @StringRes
    fun status(status: StatementStatus): Int =
        when (status) {
            StatementStatus.NOT_ISSUED -> R.string.credit_status_not_issued
            StatementStatus.DUE -> R.string.credit_status_due
            StatementStatus.PAID -> R.string.credit_status_paid
            StatementStatus.OVERDUE -> R.string.credit_status_overdue
        }
}
