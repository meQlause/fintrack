package com.meqlause.fintrack.feature.transaction.history

import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.model.Transaction
import java.time.LocalDate
import java.time.ZoneId

// FR-037: true when [transaction] passes the type, wallet, category, day and search filters
internal fun Transaction.matches(
    filter: HistoryFilter,
    date: LocalDate,
): Boolean =
    filter.type.accepts(movement.type) &&
        (filter.day == null || date.dayOfMonth == filter.day) &&
        (filter.walletId == null || filter.walletId in listOfNotNull(routing.walletId, routing.targetWalletId)) &&
        (filter.categoryId == null || routing.categoryId == filter.categoryId) &&
        matchesQuery(filter.query)

// FR-037: search reads the note and the merchant, ignoring case
private fun Transaction.matchesQuery(query: String): Boolean =
    query.isBlank() ||
        memo.note.contains(query, ignoreCase = true) ||
        memo.merchant.contains(query, ignoreCase = true)

// FR-036: the local calendar day a transaction happened on
internal fun Transaction.dateIn(zone: ZoneId): LocalDate = timing.occurredAt.atZone(zone).toLocalDate()

// BR-010: both ends of a period are inclusive
internal fun isInPeriod(
    date: LocalDate,
    period: Period,
): Boolean = date in period.startDate..period.endDate
