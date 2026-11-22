package com.meqlause.fintrack.core.domain.onboarding

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.usecase.ShouldShowOnboardingUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Preferences
import com.meqlause.fintrack.core.model.Settings
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant

class ShouldShowOnboardingUseCaseTest {
    // FR-001 AC: onboarding is shown only when no wallet exists and the onboardingCompleted flag is false
    @Test
    fun `given no wallet and onboarding not completed when observed then onboarding is shown (FR-001)`() =
        runBlocking {
            val useCase = useCaseWith(completed = false, wallets = InMemoryWalletRepository())

            assertThat(useCase().first()).isEqualTo(true)
        }

    @Test
    fun `given a wallet exists when observed then onboarding is not shown (FR-001)`() =
        runBlocking {
            val wallets = InMemoryWalletRepository()
            wallets.insert(aWallet())

            assertThat(useCaseWith(completed = false, wallets = wallets)().first()).isEqualTo(false)
        }

    @Test
    fun `given onboarding completed when observed then onboarding is not shown (FR-001)`() =
        runBlocking {
            val useCase = useCaseWith(completed = true, wallets = InMemoryWalletRepository())

            assertThat(useCase().first()).isEqualTo(false)
        }

    private fun useCaseWith(
        completed: Boolean,
        wallets: InMemoryWalletRepository,
    ): ShouldShowOnboardingUseCase {
        val settings = InMemorySettingsRepository(Settings(preferences = Preferences(onboardingCompleted = completed)))
        return ShouldShowOnboardingUseCase(settings, wallets)
    }

    private fun aWallet(): Wallet =
        Wallet(
            walletId = WalletId(0L),
            kind = WalletKind(type = WalletType.CASH, investKind = null),
            terms = WalletTerms(initialBalance = Money(0L), credit = null),
            profile = WalletProfile(name = "Tunai", appearance = Appearance("cash", "green"), sortOrder = 0),
            status = WalletStatus(),
            audit = Audit(createdAt = Instant.EPOCH, updatedAt = Instant.EPOCH),
        )
}
