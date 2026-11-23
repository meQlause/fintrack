package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.model.Audit
import kotlinx.serialization.Serializable

// DOC-11: audit stamps as ISO-8601 UTC text
@Serializable
internal data class AuditDto(
    val createdAt: String,
    val updatedAt: String,
) {
    fun toModel(): Audit = Audit(BackupValues.instant(createdAt), BackupValues.instant(updatedAt))

    companion object {
        fun from(audit: Audit): AuditDto = AuditDto(audit.createdAt.toString(), audit.updatedAt.toString())
    }
}
