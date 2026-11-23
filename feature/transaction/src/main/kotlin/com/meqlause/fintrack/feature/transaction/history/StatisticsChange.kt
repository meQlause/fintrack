package com.meqlause.fintrack.feature.transaction.history

import androidx.compose.ui.graphics.Color
import com.meqlause.fintrack.core.designsystem.theme.FintrackColors
import com.meqlause.fintrack.core.model.TransactionType

// FR-062: the change label of a whole-percent change, arrow first; zero reads as "0%"
internal fun percentText(percent: Long): String =
    when {
        percent > 0L -> "▲ $percent%"
        percent < 0L -> "▼ ${-percent}%"
        else -> "0%"
    }

// FR-062: a rise is red for expense and green for income; a fall is the other way round
internal fun changeColor(
    percent: Long,
    type: TransactionType,
    colors: FintrackColors,
): Color {
    val rise = if (type == TransactionType.INCOME) colors.semantics.income else colors.semantics.expense
    val fall = if (type == TransactionType.INCOME) colors.semantics.expense else colors.semantics.income
    return when {
        percent > 0L -> rise
        percent < 0L -> fall
        else -> colors.texts.onSurfaceVariant
    }
}
