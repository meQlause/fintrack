package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId

// S-04, S-18: the wallets, balances and categories the sheet can choose from; archived ones are never offered (BR-007)
internal data class EditCatalog(
    val wallets: List<Wallet> = emptyList(),
    val balances: Map<WalletId, Money> = emptyMap(),
    val categories: List<Category> = emptyList(),
) {
    fun activeWallets(): List<Wallet> = wallets.filterNot { it.status.isArchived }

    fun categoriesOf(kind: CategoryKind): List<Category> =
        categories
            .filter { it.profile.kind == kind && !it.state.isArchived }
            .sortedBy { it.profile.sortOrder }
}
