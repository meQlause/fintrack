package com.meqlause.fintrack.feature.credit.detail

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletType
import java.time.Instant

// BR-039: a credit wallet can never be the source of a bill payment, so only other wallets are offered
internal fun Wallet.isPayableSource(): Boolean = kind.type != WalletType.CREDIT_CARD && kind.type != WalletType.PAYLATER

// FR-138: the wallet with its statement or due day changed; only the day and the update time differ
internal fun Wallet.withCreditDay(
    picker: CreditDay,
    newDay: Int,
    updatedAt: Instant,
): Wallet {
    val credit = requireNotNull(terms.credit)
    val updated =
        if (picker == CreditDay.STATEMENT) credit.copy(statementDay = newDay) else credit.copy(dueDay = newDay)
    return copy(
        terms = terms.copy(credit = updated),
        audit = Audit(createdAt = audit.createdAt, updatedAt = updatedAt),
    )
}
