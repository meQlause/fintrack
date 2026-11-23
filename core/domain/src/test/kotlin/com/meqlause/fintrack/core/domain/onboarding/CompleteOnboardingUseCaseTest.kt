package com.meqlause.fintrack.core.domain.onboarding

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.category.DefaultCategory
import com.meqlause.fintrack.core.domain.usecase.CompleteOnboardingUseCase
import com.meqlause.fintrack.core.domain.usecase.InMemoryCategoryRepository
import com.meqlause.fintrack.core.domain.usecase.SeedDefaultCategoriesUseCase
import com.meqlause.fintrack.core.domain.validation.WalletNameError
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

/** Complete onboarding use case tests. */
class CompleteOnboardingUseCaseTest {
    private val wallets = InMemoryWalletRepository()
    private val categories = InMemoryCategoryRepository()
    private val settings = InMemorySettingsRepository()
    private val clock = Clock.fixed(Instant.parse("2026-11-22T03:00:00Z"), ZoneOffset.UTC)

    private val useCase =
        CompleteOnboardingUseCase(
            walletRepository = wallets,
            settingsRepository = settings,
            seedDefaultCategories = SeedDefaultCategoriesUseCase(categories, { it.name }, clock),
            validator = WalletNameValidator(),
            clock = clock,
        )

    // FR-002 AC: the first wallet is created with the name, type and initial balance the user entered

    /** Given valid first wallet when completed then wallet is stored with type and initial balance (FR-002). */
    @Test
    fun `given valid first wallet when completed then wallet is stored with type and initial balance (FR-002)`() =
        runBlocking {
            val result = useCase(draft(name = "Dompet Utama", type = WalletType.BANK, balance = 250_000L))

            val stored = wallets.observeAll().first().single()
            assertThat(result).isEqualTo(CompleteOnboardingResult.Completed)
            assertThat(stored.profile.name).isEqualTo("Dompet Utama")
            assertThat(stored.kind.type).isEqualTo(WalletType.BANK)
            assertThat(stored.terms.initialBalance).isEqualTo(Money(250_000L))
        }

    /** Given name with surrounding spaces when completed then the wallet name is stored trimmed (FR-002). */
    @Test
    fun `given name with surrounding spaces when completed then the wallet name is stored trimmed (FR-002)`() =
        runBlocking {
            useCase(draft(name = "  Tunai  "))

            assertThat(
                wallets
                    .observeAll()
                    .first()
                    .single()
                    .profile.name,
            ).isEqualTo("Tunai")
        }

    /** Given blank name when completed then nothing is stored and the name is rejected (FR-002). */
    @Test
    fun `given blank name when completed then nothing is stored and the name is rejected (FR-002)`() =
        runBlocking {
            val result = useCase(draft(name = "   "))

            assertThat(result).isEqualTo(CompleteOnboardingResult.NameRejected(WalletNameError.BLANK))
            assertThat(wallets.observeAll().first()).hasSize(0)
            assertThat(
                settings
                    .observe()
                    .first()
                    .preferences.onboardingCompleted,
            ).isEqualTo(false)
        }

    /** Given name longer than thirty characters when completed then the name is rejected (FR-002). */
    @Test
    fun `given name longer than thirty characters when completed then the name is rejected (FR-002)`() =
        runBlocking {
            val result = useCase(draft(name = "A".repeat(31)))

            assertThat(result).isEqualTo(CompleteOnboardingResult.NameRejected(WalletNameError.TOO_LONG))
        }

    // FR-004 AC: the default categories are seeded on first launch

    /** Given onboarding finished when completed then every default category is seeded (FR-004). */
    @Test
    fun `given onboarding finished when completed then every default category is seeded (FR-004)`() =
        runBlocking {
            useCase(draft())

            val names = categories.observeAll().first().map { it.profile.name }
            assertThat(names).hasSize(DefaultCategory.entries.size)
            assertThat(names.first()).isEqualTo(DefaultCategory.entries.first().name)
        }

    /** Given categories already exist when completed then they are not seeded again (FR-004). */
    @Test
    fun `given categories already exist when completed then they are not seeded again (FR-004)`() =
        runBlocking {
            useCase(draft(name = "Pertama"))
            val seededCount = categories.observeAll().first().size

            useCase(draft(name = "Kedua"))

            assertThat(categories.observeAll().first()).hasSize(seededCount)
        }

    // FR-001 AC: after onboarding the flag is set, so the Dashboard is shown on the next launch

    /** Given onboarding finished when completed then onboarding is marked completed (FR-001). */
    @Test
    fun `given onboarding finished when completed then onboarding is marked completed (FR-001)`() =
        runBlocking {
            useCase(draft())

            assertThat(
                settings
                    .observe()
                    .first()
                    .preferences.onboardingCompleted,
            ).isTrue()
            assertThat(wallets.observeAll().first()).hasSize(1)
        }

    /** Given wallet stored when completed then its audit timestamps come from the clock (FR-002). */
    @Test
    fun `given wallet stored when completed then its audit timestamps come from the clock (FR-002)`() =
        runBlocking {
            useCase(draft())

            val audit =
                wallets
                    .observeAll()
                    .first()
                    .single()
                    .audit
            assertThat(audit.createdAt).isEqualTo(clock.instant())
            assertThat(audit.updatedAt).isEqualTo(clock.instant())
        }

    // FR-002: each onboarding wallet type starts with its own icon and colour

    /** Given e-wallet when completed then the wallet starts with the teal e-wallet appearance (FR-002). */
    @Test
    fun `given e-wallet when completed then the wallet starts with the teal e-wallet appearance (FR-002)`() =
        runBlocking {
            useCase(draft(type = WalletType.EWALLET))

            val appearance =
                wallets
                    .observeAll()
                    .first()
                    .single()
                    .profile.appearance
            assertThat(appearance.iconKey).isEqualTo("ewallet")
            assertThat(appearance.colorKey).isEqualTo("teal")
        }

    /** Given other wallet type when completed then the wallet starts with the neutral appearance (FR-002). */
    @Test
    fun `given other wallet type when completed then the wallet starts with the neutral appearance (FR-002)`() =
        runBlocking {
            useCase(draft(type = WalletType.OTHER))

            val appearance =
                wallets
                    .observeAll()
                    .first()
                    .single()
                    .profile.appearance
            assertThat(appearance.iconKey).isEqualTo("wallet")
            assertThat(appearance.colorKey).isEqualTo("gray")
        }

    private fun draft(
        name: String = "Tunai",
        type: WalletType = WalletType.CASH,
        balance: Long = 0L,
    ) = FirstWalletDraft(name = name, type = type, initialBalance = Money(balance))
}
