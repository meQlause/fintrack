package com.meqlause.fintrack.core.testing.data

import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId

/** DOC-02, FR-004: a sample category; the kind is fixed at creation (BR-006). */
internal fun aCategory(
    name: String = "Makanan",
    kind: CategoryKind = CategoryKind.EXPENSE,
    sortOrder: Int = 0,
): Category =
    Category(
        categoryId = CategoryId(0L),
        profile =
            CategoryProfile(
                name = name,
                kind = kind,
                appearance = Appearance("food", "orange"),
                sortOrder = sortOrder,
            ),
        state = CategoryState(isDefault = true, isArchived = false),
        audit = anAudit(),
    )

/** BR-002, FR-030: a sample transaction of [type] on [walletId]; a note and merchant are always filled. */
internal fun aTransaction(
    type: TransactionType,
    walletId: WalletId,
    amount: Long = 50_000L,
    categoryId: CategoryId? = null,
): Transaction =
    Transaction(
        transactionId = TransactionId(0L),
        movement = Movement(type = type, amount = Money(amount)),
        routing = Routing(walletId = walletId, targetWalletId = null, categoryId = categoryId, loanId = null),
        memo = Memo(note = "Makan siang", merchant = "Warung Bu Tini"),
        origin = Origin(source = TransactionSource.MANUAL, inboxItemId = null),
        timing = Timing(occurredAt = anInstant(), audit = anAudit()),
    )

/** DOC-02, FR-074: a sample budget for [categoryId]; null means the overall budget. */
internal fun aBudget(
    categoryId: CategoryId?,
    limit: Long = 1_000_000L,
): Budget =
    Budget(
        budgetId = BudgetId(0L),
        categoryId = categoryId,
        limitAmount = Money(limit),
        isActive = true,
        createdAt = anInstant(),
        updatedAt = anInstant(OFFSET_BUDGET_UPDATED_MILLIS),
    )

private const val OFFSET_BUDGET_UPDATED_MILLIS: Long = 2_000L
