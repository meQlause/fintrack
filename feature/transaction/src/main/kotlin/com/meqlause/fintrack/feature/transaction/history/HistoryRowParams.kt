package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackColors
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.transaction.TransactionRowParams
import com.meqlause.fintrack.feature.transaction.R
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-036, DOC-08: a history line as the shared transaction row shows it; a row without a title is named by its type
@Composable
internal fun HistoryRowItem.toParams(): TransactionRowParams =
    TransactionRowParams(
        iconRes = DesignR.drawable.ic_wallet,
        iconColor = type.accent(FintrackTheme.colors),
        title = title ?: stringResource(typeLabelRes(type)),
        subtitle = subtitle,
        amount = amount,
        type = type,
    )

// DOC-08: the icon tint follows the money direction, like the amount beside it
private fun TransactionType.accent(colors: FintrackColors): Color =
    when (this) {
        TransactionType.EXPENSE -> colors.semantics.expense
        TransactionType.INCOME -> colors.semantics.income
        TransactionType.TRANSFER -> colors.semantics.transfer
        else -> colors.texts.onSurfaceVariant
    }

private fun typeLabelRes(type: TransactionType): Int =
    when (type) {
        TransactionType.EXPENSE -> R.string.transaction_type_expense
        TransactionType.INCOME -> R.string.transaction_type_income
        TransactionType.TRANSFER -> R.string.transaction_type_transfer
        else -> R.string.history_type_loan
    }
