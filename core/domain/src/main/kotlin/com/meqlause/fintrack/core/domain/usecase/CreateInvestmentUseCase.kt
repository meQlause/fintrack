package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.flow.first
import java.time.Instant
import javax.inject.Inject

/** FR-170: creates an INVESTMENT wallet with its kind and the amount already put in */
public class CreateInvestmentUseCase
    @Inject
    constructor(
        private val wallets: WalletRepository,
        private val validator: WalletNameValidator,
    ) {
        /**
         * FR-170: returns the new id, or null when the name is invalid or used by an active wallet, or the
         * amount is negative.
         */
        public suspend operator fun invoke(
            name: String,
            investKind: InvestKind,
            invested: Money,
            currentTime: Instant,
        ): WalletId? {
            val trimmed = name.trim()
            val allWallets = wallets.observeAll().first()
            if (!isAccepted(trimmed, invested, allWallets)) return null
            val order = (allWallets.maxOfOrNull { it.profile.sortOrder } ?: -1) + 1
            val profile =
                WalletProfile(
                    name = trimmed,
                    appearance = Appearance(iconKey = INVESTMENT_ICON, colorKey = INVESTMENT_COLOR),
                    sortOrder = order,
                )
            val kind = WalletKind(type = WalletType.INVESTMENT, investKind = investKind)
            val terms = WalletTerms(initialBalance = invested, credit = null)
            return wallets.insert(
                newWallet(kind, terms, profile, Audit(createdAt = currentTime, updatedAt = currentTime)),
            )
        }

        private fun isAccepted(
            trimmed: String,
            invested: Money,
            allWallets: List<Wallet>,
        ): Boolean =
            validator.validate(trimmed) == null &&
                invested.rupiah >= 0L &&
                allWallets.none { !it.status.isArchived && it.profile.name.equals(trimmed, ignoreCase = true) }

        // FR-170: the default icon and colour of an investment wallet, as the Dompet editor gives them
        private fun newWallet(
            kind: WalletKind,
            terms: WalletTerms,
            profile: WalletProfile,
            audit: Audit,
        ): Wallet =
            Wallet(
                walletId = WalletId(0L),
                kind = kind,
                terms = terms,
                profile = profile,
                status = WalletStatus(),
                audit = audit,
            )

        private companion object {
            const val INVESTMENT_ICON = "trend"
            const val INVESTMENT_COLOR = "gray"
        }
    }
