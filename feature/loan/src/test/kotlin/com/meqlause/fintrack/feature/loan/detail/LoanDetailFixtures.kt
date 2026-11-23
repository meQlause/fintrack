package com.meqlause.fintrack.feature.loan.detail

import com.meqlause.fintrack.core.domain.loan.LoanEntry
import com.meqlause.fintrack.core.model.Agreement
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Loan
import com.meqlause.fintrack.core.model.LoanDirection
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.LoanStatus
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
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import java.time.Instant

// S-17: the fixed instant every loan detail fixture is stamped with
internal val nowStamp: Instant = Instant.parse("2026-11-23T07:05:00Z")

// S-17: a loan entry with the given status, direction and due date
internal fun loanEntry(
    status: LoanStatus = LoanStatus.OPEN,
    direction: LoanDirection = LoanDirection.LENT,
    dueDate: Instant? = null,
): LoanEntry =
    LoanEntry(
        loan =
            Loan(
                loanId = LoanId(1L),
                agreement =
                    Agreement(
                        direction = direction,
                        personName = "Budi",
                        principal = Money(300_000L),
                        startDate = nowStamp,
                        dueDate = dueDate,
                        note = "",
                    ),
                status = status,
                closedAt = null,
                audit = Audit(nowStamp, nowStamp),
            ),
        movements = emptyList(),
    )

// S-17: a repayment-style movement of the loan
internal fun loanMovement(
    type: TransactionType,
    amount: Long,
): Transaction =
    Transaction(
        transactionId = TransactionId(amount),
        movement = Movement(type, Money(amount)),
        routing = Routing(WalletId(1L), null, null, LoanId(1L)),
        memo = Memo(),
        origin = Origin(TransactionSource.MANUAL, null),
        timing = Timing(nowStamp, Audit(nowStamp, nowStamp)),
    )

// S-17: a CASH wallet with the given key and name
internal fun loanWallet(
    walletKey: Long,
    name: String,
): Wallet =
    Wallet(
        walletId = WalletId(walletKey),
        kind = WalletKind(type = WalletType.CASH, investKind = null),
        terms = WalletTerms(initialBalance = Money(0L), credit = null),
        profile = WalletProfile(name = name, appearance = Appearance("wallet", "blue"), sortOrder = 0),
        status = WalletStatus(),
        audit = Audit(nowStamp, nowStamp),
    )
