package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.WalletEntity
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms

// DOC-02: maps a wallet row to the domain model
internal fun WalletEntity.toWallet(): Wallet =
    Wallet(
        walletId = WalletId(walletId),
        kind = WalletKind(type = identity.type, investKind = terms.investKind),
        terms = WalletTerms(initialBalance = Money(identity.initialBalance), credit = creditTerms()),
        profile =
            WalletProfile(
                name = identity.name,
                appearance = appearance.toAppearance(),
                sortOrder = state.sortOrder,
            ),
        status = WalletStatus(isIncludedInTotal = state.isIncludedInTotal, isArchived = state.isArchived),
        audit = audit.toAudit(),
    )

// DOC-02: maps a wallet to its row; absent credit terms are stored as null (BR-031)
internal fun Wallet.toEntity(): WalletEntity =
    WalletEntity(
        walletId = walletId.value,
        identity =
            WalletEntity.Identity(
                name = profile.name,
                type = kind.type,
                initialBalance = terms.initialBalance.rupiah,
            ),
        terms =
            WalletEntity.Terms(
                investKind = kind.investKind,
                creditLimit = terms.credit?.creditLimit?.rupiah,
                statementDay = terms.credit?.statementDay,
                dueDay = terms.credit?.dueDay,
            ),
        appearance = profile.appearance.toColumns(),
        state =
            WalletEntity.State(
                isIncludedInTotal = status.isIncludedInTotal,
                isArchived = status.isArchived,
                sortOrder = profile.sortOrder,
            ),
        audit = audit.toColumns(),
    )

// DOC-02, BR-032: credit terms exist only when all three of their columns are set
private fun WalletEntity.creditTerms(): CreditTerms? {
    val limit = terms.creditLimit ?: return null
    val statement = terms.statementDay ?: return null
    val dueDay = terms.dueDay ?: return null
    return CreditTerms(creditLimit = Money(limit), statementDay = statement, dueDay = dueDay)
}
