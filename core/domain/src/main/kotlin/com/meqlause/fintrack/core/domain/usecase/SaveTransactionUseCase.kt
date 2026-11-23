package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.repository.CategoryRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.domain.repository.WalletRepository
import com.meqlause.fintrack.core.domain.transaction.SaveTransactionResult
import com.meqlause.fintrack.core.domain.transaction.TransactionDraft
import com.meqlause.fintrack.core.domain.transaction.TransactionRules
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/** FR-030..FR-034, BR-001..BR-009, BR-039: validates a draft, then inserts it or updates the edited transaction. */
public class SaveTransactionUseCase
    @Inject
    constructor(
        private val transactions: TransactionRepository,
        private val wallets: WalletRepository,
        private val categories: CategoryRepository,
        private val clock: Clock,
    ) {
        /** FR-030: nothing is stored when a rule is broken; an edited transaction keeps its origin and createdAt. */
        public suspend operator fun invoke(draft: TransactionDraft): SaveTransactionResult {
            val now = Instant.now(clock)
            val walletMap = wallets.observeAll().first().associateBy { it.walletId }
            val categoryMap = categories.observeAll().first().associateBy { it.categoryId }
            val errors = TransactionRules.check(draft, walletMap, categoryMap, now)
            if (errors.isNotEmpty()) return SaveTransactionResult.Rejected(errors)
            return store(draft, buildTransaction(draft, now))
        }

        private suspend fun store(
            draft: TransactionDraft,
            transaction: Transaction,
        ): SaveTransactionResult.Saved {
            val id: TransactionId =
                if (draft.existing == null) {
                    transactions.insert(transaction)
                } else {
                    transactions.update(transaction)
                    transaction.transactionId
                }
            return SaveTransactionResult.Saved(id)
        }

        private fun buildTransaction(
            draft: TransactionDraft,
            now: Instant,
        ): Transaction {
            val existing = draft.existing
            val type = draft.movement.type
            val routing =
                Routing(
                    walletId = requireNotNull(draft.routing.walletId),
                    targetWalletId = draft.routing.targetWalletId.takeIf { type == TransactionType.TRANSFER },
                    categoryId = draft.routing.categoryId.takeIf { type != TransactionType.TRANSFER },
                    loanId = null,
                )
            return Transaction(
                transactionId = existing?.transactionId ?: TransactionId(0L),
                movement = draft.movement,
                routing = routing,
                memo = draft.memo,
                origin = existing?.origin ?: Origin(TransactionSource.MANUAL, null),
                timing =
                    Timing(
                        occurredAt = draft.occurredAt,
                        audit = Audit(createdAt = existing?.timing?.audit?.createdAt ?: now, updatedAt = now),
                    ),
            )
        }
    }
