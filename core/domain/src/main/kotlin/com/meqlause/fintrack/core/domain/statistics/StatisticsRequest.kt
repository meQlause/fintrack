package com.meqlause.fintrack.core.domain.statistics

import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.model.TransactionType
import java.time.ZoneId

/** FR-051, FR-060: which transaction type to total, for which period, and the day and zone that period came from. */
public data class StatisticsRequest(
    public val type: TransactionType,
    public val period: Period,
    public val startDay: Int,
    public val zone: ZoneId,
)
