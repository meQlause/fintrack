package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.BackupSnapshot

// DOC-11: the whole .fintrack file: header, then AES-GCM over GZIP over JSON
internal object BackupCodec {
    fun encode(
        snapshot: BackupSnapshot,
        password: String,
        appVersionName: String,
    ): ByteArray {
        val payload = BackupJson.encode(BackupPayload.from(snapshot, appVersionName))
        val header = BackupHeader(BackupCrypto.newSalt(), BackupCrypto.newIv(), BackupFormat.ITERATIONS)
        val sealed = BackupCrypto.seal(BackupGzip.compress(payload), password, header)
        return header.toBytes() + sealed
    }

    fun decode(
        bytes: ByteArray,
        password: String,
    ): BackupSnapshot {
        val header = BackupHeader.parse(bytes)
        if (bytes.size <
            BackupFormat.HEADER_BYTES + BackupFormat.TAG_BYTES
        ) {
            throw BackupException(BackupFailure.CORRUPT)
        }
        val sealed = bytes.copyOfRange(BackupFormat.HEADER_BYTES, bytes.size)
        val payload = BackupGzip.decompress(BackupCrypto.open(sealed, password, header))
        return BackupJson.decode(payload).toSnapshot()
    }
}
