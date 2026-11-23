package com.meqlause.fintrack.core.domain.repository

/** DOC-11: byte access to the file the user picked through the system file picker; the app never uses the network. */
public interface BackupFiles {
    /** DOC-11: reads the whole file; throws when it is larger than [maxBytes]. */
    public suspend fun read(
        uri: String,
        maxBytes: Long,
    ): ByteArray

    /** DOC-11: writes [bytes] over the file, replacing any previous content. */
    public suspend fun write(
        uri: String,
        bytes: ByteArray,
    )

    /** DOC-11: removes a partially written file; best effort, never throws. */
    public suspend fun delete(uri: String)
}
