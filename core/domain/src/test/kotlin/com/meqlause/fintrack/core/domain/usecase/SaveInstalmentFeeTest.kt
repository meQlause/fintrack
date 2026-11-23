package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.credit.InstalmentRequest
import com.meqlause.fintrack.core.domain.transaction.DraftRouting
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.transaction.TransactionDraft
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.TransactionType
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
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

// FR-140, BR-038: the admin fee is an EXPENSE on the card, booked to the seeded fee category and never a decoy

/** FR-140, BR-038: instalment admin fee category tests. */
class SaveInstalmentFeeTest {
    private val currentTime = Instant.parse("2026-11-23T10:00:00Z")
    private val wallets = InMemoryWalletRepository()
    private val categories = InMemoryCategoryRepository()
    private val transactions = InMemoryTransactionRepository()
    private val plans = InMemoryInstallmentPlanRepository()
    private val useCase =
        SaveInstalmentPurchaseUseCase(
            saveTransaction = SaveTransactionUseCase(transactions, wallets, categories, clock()),
            wallets = wallets,
            categories = categories,
            plans = plans,
            atomic = ImmediateAtomicRunner(),
            clock = clock(),
        )
    private val card = seedCard()
    private val food = seedCategory("food", CategoryKind.EXPENSE, isDefault = true, isArchived = false)

    /** BR-038: decoy fee categories that are not default, archived, income or differently iconed are skipped. */
    @Test
    fun givenDecoyFeeCategoriesWhenFeeSavedThenTheSeededExpenseFeeCategoryIsUsed() {
        runBlocking {
            seedCategory("fee", CategoryKind.EXPENSE, isDefault = false, isArchived = false)
            seedCategory("fee", CategoryKind.EXPENSE, isDefault = true, isArchived = true)
            seedCategory("fee", CategoryKind.INCOME, isDefault = true, isArchived = false)
            seedCategory("bolt", CategoryKind.EXPENSE, isDefault = true, isArchived = false)
            val valid = seedCategory("fee", CategoryKind.EXPENSE, isDefault = true, isArchived = false)
            val result = useCase(purchase(), InstalmentRequest(6, Money(15_000L)))
            assertThat(result).isInstanceOf(SaveTransactionResult.Saved::class)
            val feeRow = transactions.stored.last()
            assertThat(feeRow.movement.amount).isEqualTo(Money(15_000L))
            assertThat(feeRow.routing.categoryId).isEqualTo(valid)
            assertThat(feeRow.routing.walletId).isEqualTo(card)
        }
    }

    /** BR-038: without a seeded fee category the fee cannot be stored, so the whole purchase is refused. */
    @Test
    fun givenNoSeededFeeCategoryWhenFeeSavedThenPurchaseIsRefusedAndNoPlanIsStored() {
        runBlocking {
            val result = useCase(purchase(), InstalmentRequest(6, Money(15_000L)))
            assertThat(result).isInstanceOf(SaveTransactionResult.Rejected::class)
            assertThat(plans.observeAll().first()).hasSize(0)
        }
    }

    /** BR-038: a zero admin fee stores the purchase and a plan without any fee transaction. */
    @Test
    fun givenZeroAdminFeeWhenSavedThenNoFeeTransactionIsStored() {
        runBlocking {
            seedCategory("fee", CategoryKind.EXPENSE, isDefault = true, isArchived = false)
            useCase(purchase(), InstalmentRequest(6, Money(0L)))
            assertThat(transactions.stored).hasSize(1)
            assertThat(
                plans
                    .observeAll()
                    .first()
                    .single()
                    .purchase.adminFeeTransactionId,
            ).isEqualTo(null)
        }
    }

    private fun purchase(): TransactionDraft =
        TransactionDraft(
            existing = null,
            movement = Movement(TransactionType.EXPENSE, Money(300_000L)),
            routing = DraftRouting(card, null, food),
            memo = Memo(note = "Sepatu"),
            occurredAt = currentTime,
        )

    private fun seedCard(): WalletId =
        runBlocking {
            wallets.insert(
                Wallet(
                    walletId = WalletId(0L),
                    kind = WalletKind(WalletType.CREDIT_CARD, null),
                    terms = WalletTerms(Money(0L), CreditTerms(Money(10_000_000L), statementDay = 25, dueDay = 10)),
                    profile = WalletProfile("Kartu", Appearance("card", "teal"), 0),
                    status = WalletStatus(),
                    audit = Audit(currentTime, currentTime),
                ),
            )
        }

    private fun seedCategory(
        icon: String,
        kind: CategoryKind,
        isDefault: Boolean,
        isArchived: Boolean,
    ): CategoryId =
        runBlocking {
            categories.insert(
                Category(
                    categoryId = CategoryId(0L),
                    profile = CategoryProfile("Kategori", kind, Appearance(icon, "teal"), 0),
                    state = CategoryState(isDefault = isDefault, isArchived = isArchived),
                    audit = Audit(currentTime, currentTime),
                ),
            )
        }

    private fun clock(): Clock = Clock.fixed(currentTime, ZoneOffset.UTC)
}
