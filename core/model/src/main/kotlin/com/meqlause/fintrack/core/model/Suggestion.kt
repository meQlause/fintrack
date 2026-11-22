package com.meqlause.fintrack.core.model

/** DOC-02: wallet and category suggested for an inbox item, and its possible pair (BR-013, FR-091). */
public data class Suggestion(
    public val suggestedWalletId: WalletId?,
    public val suggestedCategoryId: CategoryId?,
    public val relatedInboxItemId: InboxItemId?,
)
