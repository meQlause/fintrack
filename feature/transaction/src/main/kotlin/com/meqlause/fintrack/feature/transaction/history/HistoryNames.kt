package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.WalletId

// FR-036: display names looked up while rows are built; a missing id reads as blank or null
internal class HistoryNames(
    private val wallets: Map<WalletId, String>,
    private val categories: Map<CategoryId, String>,
) {
    fun walletName(walletId: WalletId): String = wallets[walletId].orEmpty()

    fun categoryName(categoryId: CategoryId): String? = categories[categoryId]
}
