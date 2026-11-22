package com.meqlause.fintrack.core.testing.data

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType

/** DOC-02, FR-010: a sample wallet; a credit wallet when [credit] is given. */
internal fun aWallet(
    name: String = "Dompet",
    initialBalance: Long = 0L,
    sortOrder: Int = 0,
    credit: CreditTerms? = null,
): Wallet =
    Wallet(
        walletId = WalletId(value = 0L),
        kind = WalletKind(type = if (credit == null) WalletType.CASH else WalletType.CREDIT_CARD, investKind = null),
        terms = WalletTerms(initialBalance = Money(initialBalance), credit = credit),
        profile = WalletProfile(name = name, appearance = Appearance("wallet", "blue"), sortOrder = sortOrder),
        status = WalletStatus(),
        audit = anAudit(),
    )
