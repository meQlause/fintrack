package com.meqlause.fintrack.feature.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.meqlause.fintrack.core.designsystem.theme.FintrackColors
import com.meqlause.fintrack.core.designsystem.theme.FintrackTheme
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.ui.transaction.TransactionRowParams
import com.meqlause.fintrack.core.designsystem.R as DesignR

// FR-043, DOC-08: one Beranda transaction as the shared row shows it; a row without text is named by its type
@Composable
internal fun HomeRecent.toParams(): TransactionRowParams =
    TransactionRowParams(
        iconRes = DesignR.drawable.ic_wallet,
        iconColor = accentOf(type, FintrackTheme.colors),
        title = title ?: stringResource(typeLabelOf(type)),
        subtitle = stringResource(R.string.home_row_subtitle, walletName, whenLabel),
        amount = amount,
        type = type,
    )

// DOC-08: the icon tint follows the money direction, like the amount beside it
private fun accentOf(
    type: TransactionType,
    colors: FintrackColors,
): Color =
    when (type) {
        TransactionType.EXPENSE -> colors.semantics.expense
        TransactionType.INCOME -> colors.semantics.income
        TransactionType.TRANSFER -> colors.semantics.transfer
        else -> colors.texts.onSurfaceVariant
    }

private fun typeLabelOf(type: TransactionType): Int =
    when (type) {
        TransactionType.EXPENSE -> R.string.home_type_expense
        TransactionType.INCOME -> R.string.home_type_income
        TransactionType.TRANSFER -> R.string.home_type_transfer
        else -> R.string.home_type_loan
    }
