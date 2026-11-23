package com.meqlause.fintrack.core.domain.transaction

import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Transaction
import java.time.Instant

/** FR-030..FR-034: what the editor sheet holds; [existing] is the stored transaction when editing, null when adding. */
public data class TransactionDraft(
    public val existing: Transaction?,
    public val movement: Movement,
    public val routing: DraftRouting,
    public val memo: Memo,
    public val occurredAt: Instant,
)
