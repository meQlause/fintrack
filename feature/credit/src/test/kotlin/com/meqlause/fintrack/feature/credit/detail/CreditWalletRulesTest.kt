package com.meqlause.fintrack.feature.credit.detail

import assertk.assertThat
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import org.junit.Test
import java.time.Instant

// BR-039: credit wallets are never the source of a bill payment
class CreditWalletRulesTest {
    /** BR-039: only credit wallets are never offered as a bill source. */
    @Test
    fun `given credit wallets when sources are listed then they are excluded (BR-039)`() {
        val profile = WalletProfile("x", Appearance("wallet", "blue"), sortOrder = 0)
        val audit = Audit(Instant.EPOCH, Instant.EPOCH)
        val paylater =
            Wallet(
                WalletId(1L),
                WalletKind(WalletType.PAYLATER, null),
                WalletTerms(Money(0L), null),
                profile,
                WalletStatus(),
                audit,
            )
        assertThat(paylater.isPayableSource()).isFalse()
        assertThat(paylater.copy(kind = WalletKind(WalletType.CASH, null)).isPayableSource()).isTrue()
    }
}
