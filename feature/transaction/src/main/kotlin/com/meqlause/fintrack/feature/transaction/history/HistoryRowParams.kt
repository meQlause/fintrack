package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.icon.IconLook
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.transaction.TransactionRowParams
import com.meqlause.fintrack.feature.transaction.R

// FR-036, DOC-08: a history line as the shared transaction row shows it; a row without a title is named by its type
@Composable
internal fun HistoryRowItem.toParams(): TransactionRowParams =
    TransactionRowParams(
        look = IconLook(iconKey = appearance.iconKey, colorKey = appearance.colorKey),
        title = title ?: stringResource(typeLabelRes(type)),
        subtitle = subtitle,
        amount = amount,
        type = type,
        auto = auto,
    )

private fun typeLabelRes(type: TransactionType): Int =
    when (type) {
        TransactionType.EXPENSE -> R.string.transaction_type_expense
        TransactionType.INCOME -> R.string.transaction_type_income
        TransactionType.TRANSFER -> R.string.transaction_type_transfer
        else -> R.string.history_type_loan
    }
