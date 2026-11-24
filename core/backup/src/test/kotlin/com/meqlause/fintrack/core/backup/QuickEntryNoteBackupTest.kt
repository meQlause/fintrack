package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlinx.serialization.json.Json
import org.junit.Test

private const val OLD_QUICK_ENTRY =
    """{"quickEntryId":1,"template":{"title":"Kopi","type":"EXPENSE","amount":20000},""" +
        """"categoryId":1,"walletId":1,"sortOrder":0,"audit":""" +
        """{"createdAt":"2026-01-01T00:00:00Z","updatedAt":"2026-01-01T00:00:00Z"}}"""

/** DOC-11, FR-039: a backup written before the shortcut note existed still restores, with an empty note. */
class QuickEntryNoteBackupTest {
    /** FR-039: a quick entry without a note key reads as an empty note. */
    @Test
    fun oldBackupWithoutNoteRestoresWithEmptyNote() {
        val entry = Json.decodeFromString(QuickEntryDto.serializer(), OLD_QUICK_ENTRY)

        assertThat(entry.toModel().template.note).isEqualTo("")
        assertThat(entry.toModel().template.title).isEqualTo("Kopi")
    }

    /** FR-039: the note of a shortcut survives a backup round trip. */
    @Test
    fun noteSurvivesRoundTrip() {
        val snapshot = SampleSnapshot.build()
        val restored = BackupCodec.decode(BackupCodec.encode(snapshot, "rahasia-123", "1.0.0"), "rahasia-123")

        assertThat(
            restored.adjunct.quickEntries
                .single()
                .template.note,
        ).isEqualTo("tanpa gula")
    }
}
