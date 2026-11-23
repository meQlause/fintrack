package com.meqlause.fintrack.feature.credit.detail

import com.meqlause.fintrack.core.model.WalletId

// FR-132, S-15: a non-credit wallet the bill can be paid from (BR-039)
internal data class PaySource(
    val walletId: WalletId,
    val name: String,
)
