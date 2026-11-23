package com.meqlause.fintrack.feature.credit.detail

import com.meqlause.fintrack.core.model.WalletId

// FR-132, FR-133, S-15: the amount typed in the pay sheet and the chosen source; failed after a rejected save
internal data class PayDraft(
    val typedDigits: String,
    val sourceId: WalletId?,
    val failed: Boolean,
)
