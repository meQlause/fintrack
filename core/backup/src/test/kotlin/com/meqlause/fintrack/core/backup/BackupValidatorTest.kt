package com.meqlause.fintrack.core.backup

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.meqlause.fintrack.core.domain.backup.BackupFailure
import com.meqlause.fintrack.core.model.BackupSnapshot
import com.meqlause.fintrack.core.model.MerchantRuleId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import org.junit.Test

// DOC-11: every business rule violation in a crafted payload is rejected before anything is replaced
/** Backup validator tests. */
class BackupValidatorTest {
    /** Valid sample is accepted. */
    @Test
    fun validSampleIsAccepted() {
        BackupValidator.validate(SampleSnapshot.build())
    }

    /** Unknown wallet reference is rejected. */
    @Test
    fun unknownWalletReferenceIsRejected() {
        val snapshot = SampleSnapshot.build()
        val broken =
            snapshot.copy(
                ledger =
                    snapshot.ledger.copy(
                        transactions =
                            snapshot.ledger.transactions.map {
                                it.copy(routing = it.routing.copy(walletId = WalletId(99)))
                            },
                    ),
            )
        assertThat(failureOf { BackupValidator.validate(broken) }).isEqualTo(BackupFailure.CORRUPT)
    }

    /** Transfer without target wallet is rejected. */
    @Test
    fun transferWithoutTargetWalletIsRejected() {
        assertThat(failureOf { BackupValidator.validate(withTransfer { it.copy(targetWalletId = null) }) })
            .isEqualTo(BackupFailure.CORRUPT)
    }

    /** Zero amount is rejected. */
    @Test
    fun zeroAmountIsRejected() {
        val snapshot = SampleSnapshot.build()
        val broken =
            snapshot.copy(
                ledger =
                    snapshot.ledger.copy(
                        transactions =
                            snapshot.ledger.transactions.map {
                                it.copy(movement = it.movement.copy(amount = Money(0)))
                            },
                    ),
            )
        assertThat(failureOf { BackupValidator.validate(broken) }).isEqualTo(BackupFailure.CORRUPT)
    }

    /** Duplicate merchant key is rejected. */
    @Test
    fun duplicateMerchantKeyIsRejected() {
        val snapshot = SampleSnapshot.build()
        val rule = snapshot.ledger.merchantRules.single()
        val second = rule.copy(merchantRuleId = MerchantRuleId(2))
        val broken = snapshot.copy(ledger = snapshot.ledger.copy(merchantRules = listOf(rule, second)))
        assertThat(failureOf { BackupValidator.validate(broken) }).isEqualTo(BackupFailure.CORRUPT)
    }

    /** Invalid period start day is rejected. */
    @Test
    fun invalidPeriodStartDayIsRejected() {
        val snapshot = SampleSnapshot.build()
        val broken = snapshot.copy(preferences = snapshot.preferences.copy(periodStartDay = 31))
        assertThat(failureOf { BackupValidator.validate(broken) }).isEqualTo(BackupFailure.CORRUPT)
    }

    private fun withTransfer(change: (Routing) -> Routing): BackupSnapshot {
        val snapshot = SampleSnapshot.build()
        val transactions =
            snapshot.ledger.transactions.map { tx ->
                if (tx.movement.type == TransactionType.TRANSFER) tx.copy(routing = change(tx.routing)) else tx
            }
        return snapshot.copy(ledger = snapshot.ledger.copy(transactions = transactions))
    }
}
