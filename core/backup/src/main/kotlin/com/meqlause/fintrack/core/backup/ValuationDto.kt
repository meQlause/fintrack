package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.ValuationEntry
import com.meqlause.fintrack.core.model.ValuationEntryId
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.serialization.Serializable

// DOC-11: one investment valuation row in the backup
@Serializable
internal data class ValuationDto(
    val valuationId: Long,
    val walletId: Long,
    val value: Long,
    val valuedAt: String,
    val createdAt: String,
) {
    fun toModel(): ValuationEntry =
        ValuationEntry(
            valuationId = ValuationEntryId(valuationId),
            walletId = WalletId(walletId),
            value = Money(value),
            valuedAt = BackupValues.instant(valuedAt),
            createdAt = BackupValues.instant(createdAt),
        )

    companion object {
        fun from(entry: ValuationEntry): ValuationDto =
            ValuationDto(
                valuationId = entry.valuationId.value,
                walletId = entry.walletId.value,
                value = entry.value.rupiah,
                valuedAt = entry.valuedAt.toString(),
                createdAt = entry.createdAt.toString(),
            )
    }
}
