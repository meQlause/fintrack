package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.AppearanceColumns
import com.meqlause.fintrack.core.database.entity.AuditColumns
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import java.time.Instant

// DOC-02: instants are stored as epoch milliseconds in UTC, so the mapping goes through the epoch value
internal fun Long.toInstant(): Instant = Instant.ofEpochMilli(this)

// DOC-02: audit stamps are stored on every table
internal fun AuditColumns.toAudit(): Audit = Audit(createdAt = createdAt.toInstant(), updatedAt = updatedAt.toInstant())

// DOC-02: audit stamps are stored on every table
internal fun Audit.toColumns(): AuditColumns =
    AuditColumns(createdAt = createdAt.toEpochMilli(), updatedAt = updatedAt.toEpochMilli())

// DOC-02: icon and colour keys are stored as two columns
internal fun AppearanceColumns.toAppearance(): Appearance = Appearance(iconKey = iconKey, colorKey = colorKey)

// DOC-02: icon and colour keys are stored as two columns
internal fun Appearance.toColumns(): AppearanceColumns = AppearanceColumns(iconKey = iconKey, colorKey = colorKey)
