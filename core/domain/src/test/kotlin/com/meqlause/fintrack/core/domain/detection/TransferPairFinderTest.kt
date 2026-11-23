package com.meqlause.fintrack.core.domain.detection

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Suggestion
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-091, BR-013: a new payment pairs with the oldest pending opposite payment of another app within 10 minutes

/** FR-091: transfer pair finder tests. */
class TransferPairFinderTest {
    private val base = Instant.parse("2026-11-23T07:00:00Z")
    private val outgoing = itemOf("com.bca", MoneyDirection.DEBIT, 500_000L, base)

    /** FR-091: an opposite payment of another app, same amount, two minutes away is the partner. */
    @Test
    fun givenOppositePaymentInWindowWhenSearchedThenItIsThePartner() {
        val incoming = itemOf("com.gopay", MoneyDirection.CREDIT, 500_000L, base.plusSeconds(120L))
        assertThat(TransferPairFinder.findPartner(outgoing, listOf(incoming))).isEqualTo(incoming)
    }

    /** FR-091: an item of the same app is never its own partner. */
    @Test
    fun givenSameAppWhenSearchedThenNoPartner() {
        val same = itemOf("com.bca", MoneyDirection.CREDIT, 500_000L, base)
        assertThat(TransferPairFinder.findPartner(outgoing, listOf(same))).isNull()
    }

    /** FR-091: a payment in the same direction is not the other half of a transfer. */
    @Test
    fun givenSameDirectionWhenSearchedThenNoPartner() {
        val sameWay = itemOf("com.gopay", MoneyDirection.DEBIT, 500_000L, base)
        assertThat(TransferPairFinder.findPartner(outgoing, listOf(sameWay))).isNull()
    }

    /** FR-091: a different amount does not pair. */
    @Test
    fun givenDifferentAmountWhenSearchedThenNoPartner() {
        val other = itemOf("com.gopay", MoneyDirection.CREDIT, 499_000L, base)
        assertThat(TransferPairFinder.findPartner(outgoing, listOf(other))).isNull()
    }

    /** FR-091: an item that is already paired is not offered again. */
    @Test
    fun givenAlreadyPairedItemWhenSearchedThenNoPartner() {
        val pairedSuggestion = Suggestion(null, null, InboxItemId(9L))
        val paired = itemOf("com.gopay", MoneyDirection.CREDIT, 500_000L, base).copy(suggestion = pairedSuggestion)
        assertThat(TransferPairFinder.findPartner(outgoing, listOf(paired))).isNull()
    }

    /** BR-013: a payment eleven minutes away is outside the ten minute window. */
    @Test
    fun givenPaymentOutsideWindowWhenSearchedThenNoPartner() {
        val late = itemOf("com.gopay", MoneyDirection.CREDIT, 500_000L, base.plusSeconds(660L))
        assertThat(TransferPairFinder.findPartner(outgoing, listOf(late))).isNull()
    }

    /** FR-091: when several partners qualify, the oldest pending one is chosen. */
    @Test
    fun givenSeveralPartnersWhenSearchedThenTheOldestIsChosen() {
        val newer = itemOf("com.gopay", MoneyDirection.CREDIT, 500_000L, base.plusSeconds(60L))
        val older = itemOf("com.ovo", MoneyDirection.CREDIT, 500_000L, base.plusSeconds(30L))
        assertThat(TransferPairFinder.findPartner(outgoing, listOf(newer, older))).isEqualTo(older)
    }

    private fun itemOf(
        packageName: String,
        direction: MoneyDirection,
        amount: Long,
        postedAt: Instant,
    ): InboxItem =
        InboxItem(
            inboxItemId = InboxItemId(0L),
            notice =
                RawNotice(
                    packageName = packageName,
                    parserId = "generic",
                    rawTitle = "Transfer",
                    rawText = "Rp500.000",
                    postedAt = postedAt,
                    fingerprint = "fp-$postedAt-$packageName",
                ),
            parsed = ParsedAmount(Money(amount), direction, "Transfer"),
            suggestion = Suggestion(null, null, null),
            resolution = Resolution(InboxStatus.PENDING, null, null),
            audit = Audit(postedAt, postedAt),
        )
}
