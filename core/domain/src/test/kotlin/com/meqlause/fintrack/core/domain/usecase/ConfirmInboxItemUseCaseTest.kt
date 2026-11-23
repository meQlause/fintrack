package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.meqlause.fintrack.core.domain.inbox.ConfirmInboxResult
import com.meqlause.fintrack.core.domain.inbox.InboxChoice
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

// FR-087, FR-091, BR-011 … BR-013: confirming creates one transaction and remembers the merchant
class ConfirmInboxItemUseCaseTest {
    private val currentTime: Instant = Instant.parse("2026-11-23T07:05:00Z")
    private val inbox = InMemoryInboxRepository()
    private val transactions = InMemoryTransactionRepository()
    private val rules = InMemoryMerchantRuleRepository()
    private val useCase =
        ConfirmInboxItemUseCase(
            inbox,
            transactions,
            rules,
            ImmediateAtomicRunner(),
            Clock.fixed(currentTime, ZoneOffset.UTC),
        )
    private val wallet = WalletId(1L)
    private val category = CategoryId(2L)

    @Test
    fun `given a pending debit when confirmed then one expense is stored and the item is CONFIRMED (BR-011, BR-013)`() =
        runBlocking {
            val id = inbox.insert(item("a", MoneyDirection.DEBIT, "INDOMARET 123"))

            val result = useCase(InboxChoice(id, wallet, category))

            assertThat(result).isInstanceOf(ConfirmInboxResult.Confirmed::class)
            val stored = transactions.observeAll().first().single()
            assertThat(stored.movement.type).isEqualTo(TransactionType.EXPENSE)
            assertThat(inbox.findById(id)?.resolution?.status).isEqualTo(InboxStatus.CONFIRMED)
        }

    @Test
    fun `given a confirmed merchant when confirmed again then its rule is remembered (BR-012)`() =
        runBlocking {
            val id = inbox.insert(item("b", MoneyDirection.DEBIT, "Indomaret"))

            useCase(InboxChoice(id, wallet, category))

            assertThat(rules.findByKey("indomaret")?.categoryId).isEqualTo(category)
        }

    @Test
    fun `given no category when confirmed then nothing is stored (FR-087)`() =
        runBlocking {
            val id = inbox.insert(item("c", MoneyDirection.CREDIT, "Gaji"))

            val result = useCase(InboxChoice(id, wallet, null))

            assertThat(result).isEqualTo(ConfirmInboxResult.MissingCategory)
            assertThat(transactions.observeAll().first().size).isEqualTo(0)
        }

    @Test
    fun `given a paired debit and credit when confirmed as transfer then one TRANSFER links both (FR-091)`() =
        runBlocking {
            val debit = inbox.insert(item("d", MoneyDirection.DEBIT, "", related = null))
            val credit = inbox.insert(item("e", MoneyDirection.CREDIT, "", related = debit))
            inbox.linkPair(debit, credit)

            val result = useCase(InboxChoice(debit, wallet, null, WalletId(9L), asTransfer = true))

            assertThat(result).isInstanceOf(ConfirmInboxResult.Confirmed::class)
            assertThat(
                transactions
                    .observeAll()
                    .first()
                    .single()
                    .movement.type,
            ).isEqualTo(TransactionType.TRANSFER)
            assertThat(inbox.findById(credit)?.resolution?.status).isEqualTo(InboxStatus.CONFIRMED)
        }

    private fun item(
        fingerprint: String,
        direction: MoneyDirection,
        merchant: String,
        related: InboxItemId? = null,
    ): InboxItem =
        InboxItem(
            inboxItemId = InboxItemId(0L),
            notice =
                RawNotice(
                    packageName = "com.bca",
                    parserId = "bca",
                    rawTitle = "Pembayaran",
                    rawText = "Rp25.000",
                    postedAt = currentTime,
                    fingerprint = fingerprint,
                ),
            parsed = ParsedAmount(Money(25_000L), direction, merchant),
            suggestion = Suggestion(null, null, related),
            resolution = Resolution(InboxStatus.PENDING, null, null),
            audit = Audit(currentTime, currentTime),
        )
}
