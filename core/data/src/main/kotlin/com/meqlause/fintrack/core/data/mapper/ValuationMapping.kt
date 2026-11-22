package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.ValuationEntryEntity
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import com.meqlause.fintrack.core.model.WalletId

// DOC-02, FR-172: maps a valuation row to the domain model
internal fun ValuationEntryEntity.toValuationEntry(): ValuationEntry =
    ValuationEntry(
        valuationId = ValuationEntryId(valuationId),
        walletId = WalletId(walletId),
        value = Money(value),
        valuedAt = valuedAt.toInstant(),
        createdAt = createdAt.toInstant(),
    )

// DOC-02, FR-172: maps a valuation to its row
internal fun ValuationEntry.toEntity(): ValuationEntryEntity =
    ValuationEntryEntity(
        valuationId = valuationId.value,
        walletId = walletId.value,
        value = value.rupiah,
        valuedAt = valuedAt.toEpochMilli(),
        createdAt = createdAt.toEpochMilli(),
    )
