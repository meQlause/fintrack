package com.meqlause.fintrack.core.domain.networth

import com.meqlause.fintrack.core.model.Money

// BR-043, BR-053: the parts of Kekayaan bersih; every amount is whole rupiah and may be zero
public data class NetWorth(
    public val saldo: Money,
    public val investasi: Money,
    public val piutang: Money,
    public val utangKartu: Money,
    public val hutang: Money,
) {
    /** BR-053: Aset = Saldo + Investasi + Piutang. */
    public val assets: Money
        get() = saldo + investasi + piutang

    /** BR-043: Utang = Utang kartu + Hutang. */
    public val debts: Money
        get() = utangKartu + hutang

    /** BR-053: Kekayaan bersih = Aset - Utang. */
    public val total: Money
        get() = assets - debts
}
