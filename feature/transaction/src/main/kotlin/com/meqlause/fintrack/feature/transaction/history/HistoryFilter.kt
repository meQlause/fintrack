package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.WalletId

// FR-037: everything the user narrowed the list by; null or blank means no narrowing on that field
internal data class HistoryFilter(
    val type: HistoryTypeFilter = HistoryTypeFilter.EVERY,
    val walletId: WalletId? = null,
    val categoryId: CategoryId? = null,
    val day: Int? = null,
    val query: String = "",
) {
    // FR-037: true when any filter narrows the list, which turns an empty result into "Tidak ditemukan"
    val isActive: Boolean
        get() =
            type != HistoryTypeFilter.EVERY ||
                walletId != null ||
                categoryId != null ||
                day != null ||
                query.isNotBlank()
}
