package com.meqlause.fintrack.core.domain.transaction

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.WalletId

/** FR-030..FR-032: wallets and category as picked in the editor; null until the user chooses them (BR-002). */
public data class DraftRouting(
    public val walletId: WalletId?,
    public val targetWalletId: WalletId?,
    public val categoryId: CategoryId?,
)
