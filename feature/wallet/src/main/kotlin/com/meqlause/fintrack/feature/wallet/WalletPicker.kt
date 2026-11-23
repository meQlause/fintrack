package com.meqlause.fintrack.feature.wallet

// S-09, S-18: the drawer the editor has open; types and investment kinds are chosen in drawers, never chip piles
internal enum class WalletPicker {
    TYPE,
    INVEST_KIND,
    STATEMENT_DAY,
    DUE_DAY,
}
