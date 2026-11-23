package com.meqlause.fintrack.feature.wallet.component

// S-09: one legend entry of the net-worth card, its amount in rupiah and the palette key of its dot
internal data class NetWorthPart(
    val labelRes: Int,
    val amount: Long,
    val colorKey: String,
)
