package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import java.time.Instant

private val EPOCH_AUDIT = Audit(Instant.EPOCH, Instant.EPOCH)

/** FR-173: a wallet for the investment and observe use case tests; the fake repository assigns its id. */
internal fun walletOf(
    type: WalletType,
    investKind: InvestKind? = null,
    archived: Boolean = false,
    initial: Long = 0L,
): Wallet =
    Wallet(
        walletId = WalletId(0L),
        kind = WalletKind(type, investKind),
        terms = WalletTerms(Money(initial), null),
        profile = WalletProfile("Dompet", Appearance("wallet", "blue"), 0),
        status = WalletStatus(isArchived = archived),
        audit = EPOCH_AUDIT,
    )

/** FR-173: a TRANSFER row from one wallet to another, occurring at the given ISO instant. */
internal fun transferRow(
    from: WalletId,
    target: WalletId,
    amount: Long,
    atText: String,
): Transaction =
    Transaction(
        transactionId = TransactionId(0L),
        movement = Movement(TransactionType.TRANSFER, Money(amount)),
        routing = Routing(from, target, categoryId = null, loanId = null),
        memo = Memo(),
        origin = Origin(TransactionSource.MANUAL, null),
        timing = Timing(Instant.parse(atText), EPOCH_AUDIT),
    )

/** FR-172: a manual valuation row of one investment wallet. */
internal fun valuationRow(
    wallet: WalletId,
    value: Long,
    atText: String,
): ValuationEntry {
    val instant = Instant.parse(atText)
    return ValuationEntry(ValuationEntryId(0L), wallet, Money(value), instant, instant)
}
