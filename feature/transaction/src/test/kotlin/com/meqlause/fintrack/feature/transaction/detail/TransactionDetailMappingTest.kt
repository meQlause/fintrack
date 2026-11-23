package com.meqlause.fintrack.feature.transaction.detail

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.LoanId
import com.meqlause.fintrack.core.model.Memo
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.Movement
import com.meqlause.fintrack.core.model.Origin
import com.meqlause.fintrack.core.model.Routing
import com.meqlause.fintrack.core.model.Timing
import com.meqlause.fintrack.core.model.Transaction
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.TransactionSource
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.testing.time.FakeClock
import org.junit.Test
import java.time.Instant

// FR-034, FR-038, S-06: a stored transaction resolved into the labels of the detail sheet
class TransactionDetailMappingTest {
    private val clock = FakeClock(Instant.parse("2026-11-23T10:00:00Z"))
    private val walletNames = mapOf(WalletId(1L) to "BCA", WalletId(2L) to "Tunai")
    private val categoryNames = mapOf(CategoryId(7L) to "Makan")

    private fun storedRow(
        type: TransactionType = TransactionType.EXPENSE,
        memo: Memo = Memo(note = "Siang", merchant = "Warung"),
        target: WalletId? = null,
        category: CategoryId? = CategoryId(7L),
    ): Transaction =
        Transaction(
            TransactionId(3L),
            Movement(type, Money(47_000L), Money(0L)),
            Routing(WalletId(1L), target, category, loanId = null),
            memo,
            Origin(TransactionSource.MANUAL, inboxItemId = null),
            Timing(Instant.parse("2026-11-23T03:00:00Z"), Audit(Instant.EPOCH, Instant.EPOCH)),
        )

    /** FR-034: an expense shows its merchant, category, wallet and the time. */
    @Test
    fun `given an expense when resolved then its names and labels are filled (FR-034)`() {
        val detail =
            storedRow().toDetail(
                walletNames,
                categoryNames,
                clock,
                mapOf(CategoryId(7L) to Appearance("food", "orange")),
            )
        assertThat(detail.title).isEqualTo("Warung")
        assertThat(detail.categoryName).isEqualTo("Makan")
        assertThat(detail.walletLabel).isEqualTo("BCA")
        assertThat(detail.isLoanLinked).isFalse()
        assertThat(detail.appearance).isEqualTo(Appearance("food", "orange"))
        assertThat(detail.timeLabel.isNotEmpty()).isTrue()
        assertThat(detail.amount).isEqualTo(Money(47_000L))
    }

    /** FR-034: the title falls back to the category; transfers read "Dari → Ke"; loans are linked. */
    @Test
    fun `given other kinds when resolved then fallbacks and routes follow (FR-038)`() {
        val bare = storedRow(memo = Memo()).toDetail(walletNames, categoryNames, clock)
        assertThat(bare.title).isEqualTo("Makan")
        val uncategorised = storedRow(memo = Memo(), category = null).toDetail(walletNames, categoryNames, clock)
        assertThat(uncategorised.title).isEqualTo("")
        assertThat(uncategorised.categoryName).isNull()
        val transfer =
            storedRow(
                TransactionType.TRANSFER,
                target = WalletId(2L),
            ).withFee(2_500L).toDetail(walletNames, categoryNames, clock)
        assertThat(transfer.walletLabel).isEqualTo("BCA → Tunai")
        assertThat(transfer.feeAmount).isEqualTo(Money(2_500L))
        assertThat(storedRow().withLoan().toDetail(walletNames, categoryNames, clock).isLoanLinked).isTrue()
    }

    private fun Transaction.withFee(fee: Long): Transaction = copy(movement = movement.copy(feeAmount = Money(fee)))

    private fun Transaction.withLoan(): Transaction = copy(routing = routing.copy(loanId = LoanId(1L)))
}
