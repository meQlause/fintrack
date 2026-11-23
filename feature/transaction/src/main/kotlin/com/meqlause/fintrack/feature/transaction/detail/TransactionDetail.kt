package com.meqlause.fintrack.feature.transaction.detail

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType

// FR-034, FR-038, S-06: the facts of one transaction, already resolved to names and labels
internal data class TransactionDetail(
    val type: TransactionType,
    val amount: Money,
    val fee: Money,
    val title: String,
    val merchant: String,
    val note: String,
    val categoryName: String?,
    val walletLabel: String,
    val dateLabel: String,
    val timeLabel: String,
    val source: TransactionSource,
    val isLoanLinked: Boolean,
)
