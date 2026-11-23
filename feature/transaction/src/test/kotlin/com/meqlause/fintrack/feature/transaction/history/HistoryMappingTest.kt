package com.meqlause.fintrack.feature.transaction.history

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import com.meqlause.fintrack.core.domain.period.Period
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
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
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// FR-036, FR-037, BR-010, S-05: filtering and row building of the Riwayat list
class HistoryMappingTest {
    private val stamp = Instant.parse("2026-11-18T05:30:00Z")
    private val names =
        HistoryNames(
            wallets = mapOf(WalletId(1L) to "BCA", WalletId(2L) to "Tunai"),
            categories = mapOf(CategoryId(7L) to "Makan"),
            appearances = mapOf(CategoryId(7L) to Appearance("food", "orange")),
        )

    private fun spent(
        type: TransactionType = TransactionType.EXPENSE,
        memo: Memo = Memo(note = "Kopi pagi", merchant = "Starbucks"),
        category: CategoryId? = CategoryId(7L),
        target: WalletId? = null,
    ): Transaction =
        Transaction(
            TransactionId(5L),
            Movement(type, Money(30_000L)),
            Routing(WalletId(1L), target, category, loanId = null),
            memo,
            Origin(TransactionSource.MANUAL, inboxItemId = null),
            Timing(stamp, Audit(Instant.EPOCH, Instant.EPOCH)),
        )

    private val dayOne = LocalDate.parse("2026-11-18")

    /** FR-037: each part of the filter narrows the list on its own. */
    @Test
    fun `given a transaction when the filters are set then each narrows on its own (FR-037)`() {
        val sample = spent()
        assertThat(sample.matches(HistoryFilter(), dayOne)).isTrue()
        assertThat(sample.matches(HistoryFilter(type = HistoryTypeFilter.INCOME), dayOne)).isFalse()
        assertThat(sample.matches(HistoryFilter(dayOfMonth = 18), dayOne)).isTrue()
        assertThat(sample.matches(HistoryFilter(dayOfMonth = 19), dayOne)).isFalse()
        assertThat(sample.matches(HistoryFilter(walletId = WalletId(1L)), dayOne)).isTrue()
        assertThat(sample.matches(HistoryFilter(walletId = WalletId(2L)), dayOne)).isFalse()
        assertThat(spent(target = WalletId(2L)).matches(HistoryFilter(walletId = WalletId(2L)), dayOne)).isTrue()
        assertThat(sample.matches(HistoryFilter(categoryId = CategoryId(7L)), dayOne)).isTrue()
        assertThat(sample.matches(HistoryFilter(categoryId = CategoryId(8L)), dayOne)).isFalse()
    }

    /** FR-037: the search reads the note and the merchant, ignoring case. */
    @Test
    fun `given a note and a merchant when searched then either matches ignoring case (FR-037)`() {
        val sample = spent()
        assertThat(sample.matches(HistoryFilter(query = "KOPI"), dayOne)).isTrue()
        assertThat(sample.matches(HistoryFilter(query = "starbu"), dayOne)).isTrue()
        assertThat(sample.matches(HistoryFilter(query = "bensin"), dayOne)).isFalse()
        assertThat(sample.matches(HistoryFilter(query = "  "), dayOne)).isTrue()
    }

    /** BR-010: both ends of the period count, and the day is read in the device zone. */
    @Test
    fun `given a period when dates are checked then both ends are inclusive (BR-010)`() {
        val period = Period(LocalDate.parse("2026-11-01"), LocalDate.parse("2026-11-30"))
        assertThat(isInPeriod(LocalDate.parse("2026-11-01"), period)).isTrue()
        assertThat(isInPeriod(LocalDate.parse("2026-11-30"), period)).isTrue()
        assertThat(isInPeriod(LocalDate.parse("2026-12-01"), period)).isFalse()
        assertThat(spent().dateIn(ZoneOffset.UTC)).isEqualTo(dayOne)
    }

    /** FR-036: the row title falls back from merchant to note to category; transfers name both wallets. */
    @Test
    fun `given transactions when rows are built then title subtitle and look follow (FR-036)`() {
        val withMerchant = spent().toRowItem(names, ZoneOffset.UTC)
        assertThat(withMerchant.title).isEqualTo("Starbucks")
        assertThat(withMerchant.subtitle.startsWith("BCA")).isTrue()
        assertThat(withMerchant.appearance).isEqualTo(Appearance("food", "orange"))
        assertThat(spent(memo = Memo(note = "Kopi")).toRowItem(names, ZoneOffset.UTC).title).isEqualTo("Kopi")
        assertThat(spent(memo = Memo()).toRowItem(names, ZoneOffset.UTC).title).isEqualTo("Makan")
        assertThat(spent(memo = Memo(), category = null).toRowItem(names, ZoneOffset.UTC).title).isNull()
        val transfer = spent(TransactionType.TRANSFER, Memo(), category = null, target = WalletId(2L))
        assertThat(transfer.toRowItem(names, ZoneOffset.UTC).subtitle.startsWith("BCA → Tunai")).isTrue()
        assertThat(names.walletName(WalletId(99L))).isEqualTo("")
        assertThat(names.categoryName(CategoryId(99L))).isNull()
        assertThat(names.categoryAppearance(CategoryId(99L))).isNull()
    }
}
