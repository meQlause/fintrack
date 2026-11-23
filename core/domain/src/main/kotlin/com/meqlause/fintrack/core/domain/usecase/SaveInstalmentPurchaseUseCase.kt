package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.credit.InstalmentRequest
import com.meqlause.fintrack.core.domain.credit.StatementDates
import com.meqlause.fintrack.core.domain.repository.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.InstallmentPlanRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.transaction.DraftRouting
import com.meqlause.fintrack.core.domain.transaction.RollbackSignal
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.transaction.TransactionDraft
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.InstallmentPlan
import com.meqlause.fintrack.core.model.InstallmentPlanId
import com.meqlause.fintrack.core.model.InstallmentStatus
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Purchase
import com.meqlause.fintrack.core.model.Schedule
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

// FR-140, BR-011, BR-038: a credit purchase, its admin fee and its instalment plan are stored together
public class SaveInstalmentPurchaseUseCase
    @Inject
    constructor(
        private val saveTransaction: SaveTransactionUseCase,
        private val wallets: WalletRepository,
        private val categories: CategoryRepository,
        private val plans: InstallmentPlanRepository,
        private val atomic: AtomicRunner,
        private val clock: Clock,
    ) {
        /** FR-140: nothing is stored when the purchase or its fee breaks a rule; the rule errors come back instead. */
        public suspend operator fun invoke(
            draft: TransactionDraft,
            request: InstalmentRequest,
        ): SaveTransactionResult =
            try {
                atomic.atomically { store(draft, request) }
            } catch (rollback: RollbackSignal) {
                rollback.result
            }

        private suspend fun store(
            draft: TransactionDraft,
            request: InstalmentRequest,
        ): SaveTransactionResult {
            val purchaseId = saveOrRollback(draft).transactionId
            val feeId =
                if (request.adminFee.isZero) {
                    null
                } else {
                    saveOrRollback(feeDraft(draft, request.adminFee)).transactionId
                }
            plans.insert(planOf(draft, request, purchaseId, feeId))
            return SaveTransactionResult.Saved(purchaseId)
        }

        private suspend fun saveOrRollback(draft: TransactionDraft): SaveTransactionResult.Saved =
            when (val result = saveTransaction(draft)) {
                is SaveTransactionResult.Saved -> result
                is SaveTransactionResult.Rejected -> throw RollbackSignal(result)
            }

        // BR-038: the admin fee is an EXPENSE on the same credit wallet, dated with the purchase
        private suspend fun feeDraft(
            draft: TransactionDraft,
            fee: Money,
        ): TransactionDraft =
            TransactionDraft(
                existing = null,
                movement = Movement(TransactionType.EXPENSE, fee),
                routing =
                    DraftRouting(
                        walletId = draft.routing.walletId,
                        targetWalletId = null,
                        categoryId = feeCategoryId(),
                    ),
                memo = Memo(note = FEE_NOTE),
                occurredAt = draft.occurredAt,
            )

        // BR-038: the seeded "Biaya & Admin" expense category, found by its icon and default flag
        private suspend fun feeCategoryId(): CategoryId? =
            categories
                .observeAll()
                .first()
                .firstOrNull { category ->
                    category.state.isDefault &&
                        !category.state.isArchived &&
                        category.profile.kind == CategoryKind.EXPENSE &&
                        category.profile.appearance.iconKey == FEE_ICON
                }?.categoryId

        private suspend fun planOf(
            draft: TransactionDraft,
            request: InstalmentRequest,
            purchaseId: TransactionId,
            feeId: TransactionId?,
        ): InstallmentPlan {
            val walletId = requireNotNull(draft.routing.walletId) { "a plan needs a credit wallet (FR-140)" }
            val now = Instant.now(clock)
            val purchase =
                Purchase(
                    transactionId = purchaseId,
                    walletId = walletId,
                    title = draft.memo.note.ifBlank { DEFAULT_TITLE },
                    principal = draft.movement.amount,
                    adminFeeTransactionId = feeId,
                )
            val schedule =
                Schedule(
                    tenorMonths = request.tenorMonths,
                    firstStatementMonth = firstStatementMonth(draft, walletId),
                    earlyPayoffMonth = null,
                )
            return InstallmentPlan(
                installmentPlanId = InstallmentPlanId(0L),
                purchase = purchase,
                schedule = schedule,
                status = InstallmentStatus.ACTIVE,
                audit = Audit(createdAt = now, updatedAt = now),
            )
        }

        // BR-036: portion 1 is billed on the first statement whose cut-off is on or after the purchase date
        private suspend fun firstStatementMonth(
            draft: TransactionDraft,
            walletId: WalletId,
        ) = StatementDates.firstStatementMonth(
            draft.occurredAt.atZone(clock.zone).toLocalDate(),
            wallets.statementDayOf(walletId),
        )

        private companion object {
            const val DEFAULT_TITLE = "Cicilan"
            const val FEE_NOTE = "Biaya admin cicilan"
            const val FEE_ICON = "fee"
        }
    }
