package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.inbox.ConfirmInboxResult
import com.meqlause.fintrack.core.domain.inbox.InboxChoice
import com.meqlause.fintrack.core.domain.inbox.MerchantKey
import com.meqlause.fintrack.core.domain.port.AtomicRunner
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.domain.repository.MerchantRuleRepository
import com.meqlause.fintrack.core.domain.repository.TransactionRepository
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import java.time.Clock
import java.time.Instant
import javax.inject.Inject

/** FR-087, FR-091, BR-011 … BR-013: turns a pending inbox item into one transaction, atomically. */
public class ConfirmInboxItemUseCase
    @Inject
    constructor(
        private val inbox: InboxRepository,
        private val transactions: TransactionRepository,
        private val merchantRules: MerchantRuleRepository,
        private val atomic: AtomicRunner,
        private val clock: Clock,
    ) {
        /** BR-011: nothing is stored unless the item is PENDING and the choice has what the type needs. */
        public suspend operator fun invoke(choice: InboxChoice): ConfirmInboxResult {
            val item = inbox.findById(choice.itemId)
            if (item == null || item.resolution.status != InboxStatus.PENDING) return ConfirmInboxResult.NotPending
            val partner = item.suggestion.relatedInboxItemId?.let { inbox.findById(it) }
            val pending = partner?.takeIf { it.resolution.status == InboxStatus.PENDING }
            return if (choice.asTransfer && pending != null) {
                confirmTransfer(choice, item, pending)
            } else {
                confirmSingle(choice, item)
            }
        }

        // BR-013: a DEBIT item is an expense and a CREDIT item an income; BR-012: the merchant is remembered
        private suspend fun confirmSingle(
            choice: InboxChoice,
            item: InboxItem,
        ): ConfirmInboxResult {
            val walletId = choice.walletId ?: return ConfirmInboxResult.MissingWallet
            val categoryId = choice.categoryId ?: return ConfirmInboxResult.MissingCategory
            val now = Instant.now(clock)
            val type =
                if (item.parsed.direction ==
                    MoneyDirection.DEBIT
                ) {
                    TransactionType.EXPENSE
                } else {
                    TransactionType.INCOME
                }
            val routing = Routing(walletId = walletId, targetWalletId = null, categoryId = categoryId, loanId = null)
            val transaction = transactionOf(item, type, routing, now)
            return atomic.atomically {
                val id = transactions.insert(transaction)
                inbox.resolve(item.inboxItemId, confirmed(id, now))
                rememberMerchant(item, walletId, categoryId, now)
                ConfirmInboxResult.Confirmed(id)
            }
        }

        // FR-091, BR-013: one TRANSFER; both items point at the same transaction and are CONFIRMED together
        private suspend fun confirmTransfer(
            choice: InboxChoice,
            item: InboxItem,
            partner: InboxItem,
        ): ConfirmInboxResult {
            val source = choice.walletId ?: return ConfirmInboxResult.MissingWallet
            val target = choice.targetWalletId ?: return ConfirmInboxResult.MissingWallet
            val now = Instant.now(clock)
            val routing = Routing(walletId = source, targetWalletId = target, categoryId = null, loanId = null)
            val transaction = transactionOf(item, TransactionType.TRANSFER, routing, now)
            return atomic.atomically {
                val id = transactions.insert(transaction)
                inbox.resolve(item.inboxItemId, confirmed(id, now))
                inbox.resolve(partner.inboxItemId, confirmed(id, now))
                ConfirmInboxResult.Confirmed(id)
            }
        }

        // BR-012: one rule per normalized merchant; a confirmed merchant replaces the old category and wallet
        private suspend fun rememberMerchant(
            item: InboxItem,
            walletId: WalletId,
            categoryId: CategoryId,
            currentTime: Instant,
        ) {
            val key = MerchantKey.of(item.parsed.merchant)
            if (key.isEmpty()) return
            val existing = merchantRules.findByKey(key)
            if (existing == null) {
                merchantRules.insert(
                    MerchantRule(MerchantRuleId(0L), key, categoryId, walletId, 1, Audit(currentTime, currentTime)),
                )
            } else {
                merchantRules.update(
                    existing.copy(
                        categoryId = categoryId,
                        walletId = walletId,
                        hitCount = existing.hitCount + 1,
                        audit = existing.audit.copy(updatedAt = currentTime),
                    ),
                )
            }
        }

        private fun transactionOf(
            item: InboxItem,
            type: TransactionType,
            routing: Routing,
            currentTime: Instant,
        ): Transaction =
            Transaction(
                transactionId = TransactionId(0L),
                movement = Movement(type = type, amount = item.parsed.amount),
                routing = routing,
                memo = Memo(merchant = item.parsed.merchant),
                origin = Origin(TransactionSource.NOTIFICATION, item.inboxItemId),
                timing =
                    Timing(
                        occurredAt = item.notice.postedAt,
                        audit = Audit(createdAt = currentTime, updatedAt = currentTime),
                    ),
            )

        private fun confirmed(
            transactionId: TransactionId,
            currentTime: Instant,
        ): Resolution = Resolution(InboxStatus.CONFIRMED, transactionId = transactionId, resolvedAt = currentTime)
    }
