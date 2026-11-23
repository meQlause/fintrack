package com.meqlause.fintrack.core.domain.budget

import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Budget
import com.meqlause.fintrack.core.model.BudgetId
import com.meqlause.fintrack.core.model.CategoryId
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
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal val TEST_ZONE: ZoneId = ZoneId.of("UTC")

// a clock that always reads the given UTC instant
internal fun clockAt(isoInstant: String): Clock = Clock.fixed(Instant.parse(isoInstant), TEST_ZONE)

// a budget for [categoryId]; null is the overall budget (FR-074)
internal fun aBudget(
    limit: Long = 1_000L,
    categoryId: Long? = CATEGORY_FOOD,
): Budget =
    Budget(
        budgetId = BudgetId(0L),
        categoryId = categoryId?.let(::CategoryId),
        limitAmount = Money(limit),
        isActive = true,
        createdAt = Instant.EPOCH,
        updatedAt = Instant.EPOCH,
    )

// one money movement dated at noon UTC on [date]; the category is optional
internal fun aMovement(
    amount: Long,
    type: TransactionType = TransactionType.EXPENSE,
    categoryId: Long? = CATEGORY_FOOD,
    date: String = "2026-11-10",
): Transaction =
    Transaction(
        transactionId = TransactionId(0L),
        movement = Movement(type = type, amount = Money(amount)),
        routing = Routing(WalletId(1L), null, categoryId?.let(::CategoryId), null),
        memo = Memo(),
        origin = Origin(TransactionSource.MANUAL, null),
        timing = Timing(Instant.parse("${date}T12:00:00Z"), Audit(Instant.EPOCH, Instant.EPOCH)),
    )

internal const val CATEGORY_FOOD: Long = 10L
internal const val CATEGORY_TRANSPORT: Long = 11L
