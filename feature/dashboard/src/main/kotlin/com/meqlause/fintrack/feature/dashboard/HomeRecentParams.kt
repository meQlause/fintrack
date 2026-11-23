package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.transaction.TransactionRowParams

// FR-043, DOC-08: one Beranda transaction as the shared row shows it; a row without text is named by its type
@Composable
internal fun HomeRecent.toParams(): TransactionRowParams =
    TransactionRowParams(
        iconKey = style.appearance.iconKey,
        colorKey = style.appearance.colorKey,
        title = title ?: stringResource(typeLabelOf(type)),
        subtitle = stringResource(R.string.home_row_subtitle, stamp.walletName, stamp.whenLabel),
        amount = amount,
        type = type,
        auto = style.auto,
    )

private fun typeLabelOf(type: TransactionType): Int =
    when (type) {
        TransactionType.EXPENSE -> R.string.home_type_expense
        TransactionType.INCOME -> R.string.home_type_income
        TransactionType.TRANSFER -> R.string.home_type_transfer
        else -> R.string.home_type_loan
    }
