package com.meqlause.fintrack.core.domain.statistics

import com.meqlause.fintrack.core.model.Money

private const val WHOLE_PERCENT = 100.0

// FR-062: whole percent from [previous] to [current], rounded half up; null when there is nothing to compare with
internal fun percentChangeOf(
    current: Money,
    previous: Money,
): Long? =
    if (previous.isZero) {
        null
    } else {
        Math.round((current.rupiah - previous.rupiah) * WHOLE_PERCENT / previous.rupiah)
    }
