package com.meqlause.fintrack.core.model

/** DOC-02: whether the wallet counts in the total and whether it is archived (BR-007, BR-043). */
public data class WalletStatus(
    public val isIncludedInTotal: Boolean = true,
    public val isArchived: Boolean = false,
)
