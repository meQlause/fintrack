package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.serialization.Serializable

// DOC-11: one wallet row in the backup; nested groups mirror the domain model
@Serializable
internal data class WalletDto(
    val walletId: Long,
    val kind: KindDto,
    val terms: TermsDto,
    val profile: ProfileDto,
    val status: StatusDto,
    val audit: AuditDto,
) {
    fun toModel(): Wallet =
        Wallet(
            walletId = WalletId(walletId),
            kind = kind.toModel(),
            terms = terms.toModel(),
            profile = profile.toModel(),
            status = WalletStatus(status.isIncludedInTotal, status.isArchived),
            audit = audit.toModel(),
        )

    companion object {
        fun from(wallet: Wallet): WalletDto =
            WalletDto(
                walletId = wallet.walletId.value,
                kind = KindDto(wallet.kind.type.name, wallet.kind.investKind?.name),
                terms = TermsDto.from(wallet.terms),
                profile = ProfileDto.from(wallet.profile),
                status = StatusDto(wallet.status.isIncludedInTotal, wallet.status.isArchived),
                audit = AuditDto.from(wallet.audit),
            )
    }

    @Serializable
    internal data class KindDto(
        val type: String,
        val investKind: String?,
    ) {
        fun toModel(): WalletKind =
            WalletKind(
                type = BackupValues.enumNamed<WalletType>(type),
                investKind = investKind?.let { BackupValues.enumNamed<InvestKind>(it) },
            )
    }

    @Serializable
    internal data class TermsDto(
        val initialBalance: Long,
        val credit: CreditDto?,
    ) {
        fun toModel(): WalletTerms = WalletTerms(Money(initialBalance), credit?.let { CreditDto.toCredit(it) })

        companion object {
            fun from(terms: WalletTerms): TermsDto =
                TermsDto(terms.initialBalance.rupiah, terms.credit?.let(CreditDto::from))
        }
    }

    @Serializable
    internal data class CreditDto(
        val creditLimit: Long,
        val statementDay: Int,
        val dueDay: Int,
    ) {
        companion object {
            fun toCredit(dto: CreditDto): CreditTerms =
                CreditTerms(Money(dto.creditLimit), dto.statementDay, dto.dueDay)

            fun from(credit: CreditTerms): CreditDto =
                CreditDto(credit.creditLimit.rupiah, credit.statementDay, credit.dueDay)
        }
    }

    @Serializable
    internal data class ProfileDto(
        val name: String,
        val appearance: AppearanceDto,
        val sortOrder: Int,
    ) {
        fun toModel(): WalletProfile = WalletProfile(name, appearance.toModel(), sortOrder)

        companion object {
            fun from(profile: WalletProfile): ProfileDto =
                ProfileDto(profile.name, AppearanceDto.from(profile.appearance), profile.sortOrder)
        }
    }

    @Serializable
    internal data class StatusDto(
        val isIncludedInTotal: Boolean,
        val isArchived: Boolean,
    )
}
