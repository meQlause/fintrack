package com.meqlause.fintrack.core.domain.credit

import com.meqlause.fintrack.core.model.CreditTerms
import com.meqlause.fintrack.core.model.Money
import java.time.LocalDate

/** BR-031 to BR-036: everything statement math needs for one credit wallet, passed as plain data. */
public data class CreditAccount(
    public val terms: CreditTerms,
    public val initialBalance: Money,
    public val createdOn: LocalDate,
    public val entries: List<CreditEntry>,
    public val instalments: List<CreditInstalment>,
)
