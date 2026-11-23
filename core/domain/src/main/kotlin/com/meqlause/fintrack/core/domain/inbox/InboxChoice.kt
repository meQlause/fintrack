package com.meqlause.fintrack.core.domain.inbox

import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.WalletId

/** FR-087, FR-091: what the user chose on an inbox card; [targetWalletId] and [asTransfer] matter for a pair only. */
public data class InboxChoice(
    public val itemId: InboxItemId,
    public val walletId: WalletId?,
    public val categoryId: CategoryId?,
    public val targetWalletId: WalletId? = null,
    public val asTransfer: Boolean = false,
)
