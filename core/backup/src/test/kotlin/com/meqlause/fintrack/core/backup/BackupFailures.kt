package com.meqlause.fintrack.core.backup

import com.meqlause.fintrack.core.domain.backup.BackupException
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import org.junit.Assert.fail

// DOC-11: runs [block] and returns the failure reason it throws; fails the test when nothing is thrown
internal fun failureOf(block: () -> Unit): BackupFailure {
    try {
        block()
    } catch (error: BackupException) {
        return error.failure
    }
    fail("expected a BackupException")
    error("unreachable")
}
