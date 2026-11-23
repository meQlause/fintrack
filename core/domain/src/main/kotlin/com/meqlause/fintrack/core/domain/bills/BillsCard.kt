package com.meqlause.fintrack.core.domain.bills

import com.meqlause.fintrack.core.model.Money

// FR-137, FR-146: everything the Tagihan card of Beranda shows, computed from one snapshot
public data class BillsCard(
    public val bills: List<CreditBill>,
    public val instalmentsDue: Money,
)
