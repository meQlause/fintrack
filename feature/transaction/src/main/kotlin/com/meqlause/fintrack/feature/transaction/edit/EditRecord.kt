package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.Transaction
import java.time.Instant

// FR-034: the stored transaction being edited (null while adding), the moment it is dated to and its label
internal data class EditRecord(
    val existing: Transaction?,
    val occurredAt: Instant,
    val dateLabel: String,
)
