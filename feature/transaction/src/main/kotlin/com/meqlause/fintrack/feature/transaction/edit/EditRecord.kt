package com.meqlause.fintrack.feature.transaction.edit

import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.ui.format.DateFormatter
import java.time.Clock
import java.time.Instant

// FR-034: the stored transaction being edited (null while adding), the moment it is dated to and its label
internal data class EditRecord(
    val existing: Transaction?,
    val occurredAt: Instant,
    val dateLabel: String,
)

// FR-034: the record of a sheet; a new sheet is dated to now, an edited one keeps its own moment
internal fun recordFor(
    existing: Transaction?,
    clock: Clock,
): EditRecord {
    val instant = existing?.timing?.occurredAt ?: Instant.now(clock)
    val zoned = instant.atZone(clock.zone)
    val label = "${DateFormatter.dayLabel(zoned.toLocalDate(), clock)}, ${DateFormatter.timeLabel(zoned.toLocalTime())}"
    return EditRecord(existing = existing, occurredAt = instant, dateLabel = label)
}
