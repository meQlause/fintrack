package com.meqlause.fintrack.core.database.entity

/** DOC-02: audit timestamps every table carries, as epoch milliseconds. */
data class AuditColumns(
    val createdAt: Long,
    val updatedAt: Long,
)
