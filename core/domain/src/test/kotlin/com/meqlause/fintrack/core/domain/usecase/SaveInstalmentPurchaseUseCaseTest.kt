package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.credit.InstalmentRequest
import com.meqlause.fintrack.core.domain.transaction.DraftRouting
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.transaction.TransactionDraft
import com.meqlause.fintrack.core.domain.transaction.TransactionError
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.InstallmentStatus
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
import java.time.YearMonth
import java.time.ZoneOffset

/** FR-140, BR-011, BR-036, BR-038: a credit purchase, its admin fee and its plan are stored together or not at all. */
class SaveInstalmentPurchaseUseCaseTest {
    private val currentTime = Instant.parse("2026-11-23T10:00:00Z")
    private val clock = Clock.fixed(currentTime, ZoneOffset.UTC)
    private val wallets = InMemoryWalletRepository()
    private val categories = InMemoryCategoryRepository()
    private val transactions = InMemoryTransactionRepository()
    private val plans = InMemoryInstallmentPlanRepository()
    private val foodId = seedCategory("food")
    private val feeId = seedCategory("fee")
    private val cardId = seedCard()
    private val useCase =
        SaveInstalmentPurchaseUseCase(
            saveTransaction = SaveTransactionUseCase(transactions, wallets, categories, clock),
            wallets = wallets,
            categories = categories,
            plans = plans,
            atomic = ImmediateAtomicRunner(),
            clock = clock,
        )

    /** BR-036: a purchase made before the cut-off is billed from the statement of its own month. */
    @Test
    fun `given credit purchase with tenor when saving then purchase and plan are stored (BR-036)`() =
        runBlocking {
            val result = useCase(expense(3_000_000L), InstalmentRequest(6, Money(0L)))
            assertThat(result).isInstanceOf(SaveTransactionResult.Saved::class)
            val plan = plans.observeAll().first().single()
            assertThat(plan.schedule.tenorMonths).isEqualTo(6)
            assertThat(plan.schedule.firstStatementMonth).isEqualTo(YearMonth.of(2026, 11))
            assertThat(plan.schedule.earlyPayoffMonth).isNull()
            assertThat(plan.status).isEqualTo(InstallmentStatus.ACTIVE)
            assertThat(plan.purchase.principal).isEqualTo(Money(3_000_000L))
            assertThat(plan.purchase.adminFeeTransactionId).isNull()
        }

    /** BR-038: a fee is stored as its own EXPENSE on the same card, linked from the plan. */
    @Test
    fun `given admin fee when saving then the fee is a separate expense on the card (BR-038)`() =
        runBlocking {
            useCase(expense(3_000_000L), InstalmentRequest(3, Money(15_000L)))
            val fee = transactions.stored.single { it.movement.amount == Money(15_000L) }
            assertThat(fee.movement.type).isEqualTo(TransactionType.EXPENSE)
            assertThat(fee.routing.walletId).isEqualTo(cardId)
            assertThat(fee.routing.categoryId).isEqualTo(feeId)
            val linked =
                plans
                    .observeAll()
                    .first()
                    .single()
                    .purchase.adminFeeTransactionId
            assertThat(linked).isEqualTo(fee.transactionId)
        }

    /** BR-011: a purchase that breaks a rule stores neither the transaction nor the plan. */
    @Test
    fun `given rejected purchase when saving then nothing is stored (BR-011)`() =
        runBlocking {
            val result = useCase(expense(0L), InstalmentRequest(6, Money(0L)))
            assertThat(result).isEqualTo(SaveTransactionResult.Rejected(setOf(TransactionError.AMOUNT_REQUIRED)))
            assertThat(transactions.stored).isEmpty()
            assertThat(plans.observeAll().first()).isEmpty()
        }

    private fun expense(amount: Long): TransactionDraft =
        TransactionDraft(
            existing = null,
            movement = Movement(TransactionType.EXPENSE, Money(amount)),
            routing = DraftRouting(cardId, null, foodId),
            memo = Memo(note = "HP baru"),
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

    private fun seedCategory(icon: String): CategoryId =
        runBlocking {
            categories.insert(
                Category(
                    categoryId = CategoryId(0L),
                    profile = CategoryProfile("Kategori", CategoryKind.EXPENSE, Appearance(icon, "teal"), 0),
                    state = CategoryState(isDefault = true, isArchived = false),
                    audit = Audit(currentTime, currentTime),
                ),
            )
        }
}
