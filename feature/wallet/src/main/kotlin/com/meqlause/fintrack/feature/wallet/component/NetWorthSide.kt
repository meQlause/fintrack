package com.meqlause.fintrack.feature.wallet.component

// BR-053: what one side of the net worth card shows, its title, its total and the parts under it
internal data class NetWorthSide(
    val title: String,
    val total: String,
    val rows: List<NetWorthPart>,
    val isDebt: Boolean,
)
