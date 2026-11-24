package com.meqlause.fintrack.feature.wallet

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.networth.NetWorth
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.feature.wallet.component.NetWorthParts
import org.junit.Test

// BR-043, BR-053: the Aset and Utang totals of the card follow the domain sums, negative Saldo included
class NetWorthPartsTest {
    private fun netWorth(
        saldo: Long,
        card: Long = 0L,
    ): NetWorth = NetWorth(Money(saldo), Money(0L), Money(0L), Money(card), Money(0L))

    @Test
    fun givenNegativeSaldoWhenPartsBuiltThenAssetsShowTheNegativeSum() {
        val parts = NetWorthParts.from(netWorth(saldo = -25_000L))

        assertThat(parts.assetTotal).isEqualTo("−Rp25rb")
    }

    @Test
    fun givenNoDebtWhenPartsBuiltThenDebtTotalHasNoSign() {
        val parts = NetWorthParts.from(netWorth(saldo = 0L))

        assertThat(parts.debtTotal).isEqualTo("Rp0")
    }

    @Test
    fun givenCardDebtWhenPartsBuiltThenDebtTotalIsNegative() {
        val parts = NetWorthParts.from(netWorth(saldo = 0L, card = 3_500_000L))

        assertThat(parts.debtTotal).isEqualTo("−Rp3,5jt")
    }
}
