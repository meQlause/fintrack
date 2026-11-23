package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.validation.WalletNameValidator
import com.meqlause.fintrack.core.model.InvestKind
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-170: an investment wallet is created with its kind, the amount already put in, and the default look

/** Create investment use case tests. */
class CreateInvestmentUseCaseTest {
    private val now = Instant.parse("2026-11-23T02:00:00Z")
    private val wallets = InMemoryWalletRepository()
    private val create = CreateInvestmentUseCase(wallets, WalletNameValidator())

    /** FR-170: given a valid name when created then an investment wallet with default look is stored. */
    @Test
    fun givenValidNameWhenCreatedThenInvestmentWalletWithDefaultLookIsStored() {
        runBlocking {
            val id = create("Reksa", InvestKind.MUTUAL_FUND, Money(500_000L), now)
            val stored = wallets.observeAll().first().single()
            assertThat(id).isEqualTo(stored.walletId)
            assertThat(stored.kind.type).isEqualTo(WalletType.INVESTMENT)
            assertThat(stored.kind.investKind).isEqualTo(InvestKind.MUTUAL_FUND)
            assertThat(stored.terms.initialBalance).isEqualTo(Money(500_000L))
            assertThat(stored.profile.appearance.iconKey).isEqualTo("trend")
            assertThat(stored.profile.appearance.colorKey).isEqualTo("gray")
            assertThat(stored.audit.createdAt).isEqualTo(now)
        }
    }

    /** FR-170: given a name with spaces when created then the stored name is trimmed. */
    @Test
    fun givenNameWithSpacesWhenCreatedThenStoredNameIsTrimmed() {
        runBlocking {
            create("  Emas  ", InvestKind.GOLD, Money(0L), now)
            assertThat(wallets.observeAll().first().single().profile.name).isEqualTo("Emas")
        }
    }

    /** FR-170: given a second wallet when created then it is placed after the last one. */
    @Test
    fun givenSecondWalletWhenCreatedThenSortOrderFollowsTheLastOne() {
        runBlocking {
            create("Saham", InvestKind.STOCK, Money(0L), now)
            create("Kripto", InvestKind.CRYPTO, Money(0L), now)
            val orders = wallets.observeAll().first().map { it.profile.sortOrder }
            assertThat(orders).isEqualTo(listOf(0, 1))
        }
    }

    /** FR-170: given a blank name when created then nothing is stored. */
    @Test
    fun givenBlankNameWhenCreatedThenNothingIsStored() {
        runBlocking {
            assertThat(create("   ", InvestKind.STOCK, Money(0L), now)).isNull()
            assertThat(wallets.observeAll().first()).isEqualTo(emptyList<Wallet>())
        }
    }

    /** FR-170: given a negative amount when created then nothing is stored. */
    @Test
    fun givenNegativeAmountWhenCreatedThenNothingIsStored() {
        runBlocking {
            assertThat(create("Saham", InvestKind.STOCK, Money(-1L), now)).isNull()
            assertThat(wallets.observeAll().first()).isEqualTo(emptyList<Wallet>())
        }
    }

    /** FR-170: given a name used by an active wallet in other case when created then it is rejected. */
    @Test
    fun givenNameUsedByActiveWalletInOtherCaseWhenCreatedThenRejected() {
        runBlocking {
            create("Saham", InvestKind.STOCK, Money(0L), now)
            assertThat(create("SAHAM", InvestKind.STOCK, Money(0L), now)).isNull()
            assertThat(wallets.observeAll().first().size).isEqualTo(1)
        }
    }

    /** FR-170: given a name used by an archived wallet when created then it is accepted. */
    @Test
    fun givenNameUsedByArchivedWalletWhenCreatedThenAccepted() {
        runBlocking {
            create("Saham", InvestKind.STOCK, Money(0L), now)
            val stored = wallets.observeAll().first().single()
            wallets.update(stored.copy(status = WalletStatus(isArchived = true)))
            assertThat(create("Saham", InvestKind.STOCK, Money(0L), now)).isNotNull()
        }
    }
}
