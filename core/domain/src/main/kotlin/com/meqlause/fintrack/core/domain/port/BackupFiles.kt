package com.meqlause.fintrack.core.domain.port

/** DOC-11: byte access to the file the user picked through the system file picker; the app never uses the network. */
public interface BackupFiles {
    /** DOC-11: reads the whole file; throws when it is larger than [maxBytes]. */
    public suspend fun read(
        fileUri: String,
        maxBytes: Long,
    ): ByteArray

    /** DOC-11: writes [bytes] over the file, replacing any previous content. */
    public suspend fun write(
        fileUri: String,
        bytes: ByteArray,
    )

    /** DOC-11: removes a partially written file; best effort, never throws. */
    public suspend fun delete(fileUri: String)
}
