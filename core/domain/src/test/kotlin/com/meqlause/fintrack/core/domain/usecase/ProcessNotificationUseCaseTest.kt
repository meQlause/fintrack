package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.detection.DetectedItemFactory
import com.meqlause.fintrack.core.domain.detection.ProcessNotificationResult
import com.meqlause.fintrack.core.domain.detection.StubNotificationParser
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MerchantRuleId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.MonitoredApp
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.parser.IgnoreReason
import com.meqlause.fintrack.core.parser.NotificationParserRegistry
import com.meqlause.fintrack.core.parser.ParseResult
import com.meqlause.fintrack.core.parser.ParsedTransaction
import com.meqlause.fintrack.core.testing.repository.FakeInboxRepository
import com.meqlause.fintrack.core.testing.repository.FakeMerchantRuleRepository
import com.meqlause.fintrack.core.testing.repository.FakeMonitoredAppRepository
import com.meqlause.fintrack.core.testing.time.FakeClock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Instant

// FR-084, FR-086, FR-088, FR-091: detected payments are stored as PENDING, deduplicated and paired
class ProcessNotificationUseCaseTest {
    private val start: Instant = Instant.parse("2026-11-23T10:00:00Z")
    private val clock = FakeClock(start)
    private val inbox = FakeInboxRepository()
    private val rules = FakeMerchantRuleRepository()
    private val apps = FakeMonitoredAppRepository()
    private val bcaDefaultWallet = WalletId(7)

    @Test
    fun `given a stored payment when the same notification is reposted then it is discarded (FR-086)`() =
        runBlocking {
            registerApps()
            val useCase = useCase(debitParser(merchant = "INDOMARET KEMANG 12"))

            useCase(BCA, "Pembayaran", "Pembayaran Rp25.000 di INDOMARET KEMANG 12", start)
            val repost = useCase(BCA, "Pembayaran", "Pembayaran Rp25.000 di INDOMARET KEMANG 12", start)

            assertThat(repost).isEqualTo(ProcessNotificationResult.Duplicate)
            assertThat(inbox.observeAll().first().size).isEqualTo(1)
        }

    @Test
    fun `given a detected payment then it is stored as PENDING with the app default wallet (FR-084, FR-082)`() =
        runBlocking {
            registerApps()
            val useCase = useCase(debitParser(merchant = "INDOMARET KEMANG 12"))

            val result = useCase(BCA, "Pembayaran", "Pembayaran Rp25.000 di INDOMARET KEMANG 12", start)

            assertThat(result).isInstanceOf(ProcessNotificationResult.Stored::class)
            val item = inbox.observeAll().first().single()
            assertThat(item.resolution.status).isEqualTo(InboxStatus.PENDING)
            assertThat(item.suggestion.suggestedWalletId).isEqualTo(bcaDefaultWallet)
            assertThat(item.suggestion.suggestedCategoryId).isNull()
            assertThat(item.parsed.merchant).isEqualTo("INDOMARET KEMANG 12")
        }

    @Test
    fun `given a remembered merchant then its category and wallet are suggested (FR-088)`() =
        runBlocking {
            registerApps()
            rules.insert(rule(key = "indomaret kemang", category = CategoryId(3), wallet = WalletId(9)))
            val useCase = useCase(debitParser(merchant = "INDOMARET KEMANG 12"))

            useCase(BCA, "Pembayaran", "Pembayaran Rp25.000 di INDOMARET KEMANG 12", start)

            val item = inbox.observeAll().first().single()
            assertThat(item.suggestion.suggestedCategoryId).isEqualTo(CategoryId(3))
            assertThat(item.suggestion.suggestedWalletId).isEqualTo(WalletId(9))
        }

    @Test
    fun `given a debit and a credit of one amount from two apps within 10 minutes then they are paired (FR-091)`() =
        runBlocking {
            registerApps()
            val useCase = useCase(debitParser(merchant = "GOPAY TOPUP"), creditParser())
            val first = useCase(BCA, "Transfer", "Transfer ke GoPay Rp100.000", start)
            val second = useCase(GOPAY, "Transfer", "Transfer dari BCA Rp100.000", start.plusSeconds(300))

            val firstId = (first as ProcessNotificationResult.Stored).inboxItemId
            val stored = second as ProcessNotificationResult.Stored
            assertThat(stored.pairedWith).isEqualTo(firstId)
            assertThat(itemById(firstId).suggestion.relatedInboxItemId).isEqualTo(stored.inboxItemId)
            assertThat(itemById(stored.inboxItemId).suggestion.relatedInboxItemId).isEqualTo(firstId)
        }

    @Test
    fun `given one amount more than 10 minutes apart then no pair is suggested (FR-091)`() =
        runBlocking {
            registerApps()
            val useCase = useCase(debitParser(merchant = "GOPAY TOPUP"), creditParser())
            useCase(BCA, "Transfer", "Transfer ke GoPay Rp100.000", start)
            val later = useCase(GOPAY, "Transfer", "Transfer dari BCA Rp100.000", start.plusSeconds(660))

            assertThat((later as ProcessNotificationResult.Stored).pairedWith).isNull()
        }

    @Test
    fun `given an ignored notification then nothing is stored (FR-085)`() =
        runBlocking {
            registerApps()
            val useCase = useCase(ignoringParser())

            val result = useCase(BCA, "OTP", "Kode OTP Anda 123456", start)

            assertThat(result).isEqualTo(ProcessNotificationResult.NotTransaction)
            assertThat(inbox.observeAll().first().isEmpty()).isEqualTo(true)
        }

    @Test
    fun `given a notification from an app that is not monitored then nothing is read (FR-090)`() =
        runBlocking {
            registerApps()
            val useCase = useCase(debitParser(merchant = "INDOMARET"))

            val result = useCase("com.unknown.app", "Pembayaran", "Pembayaran Rp25.000", start)

            assertThat(result).isEqualTo(ProcessNotificationResult.NotMonitored)
            assertThat(inbox.observeAll().first().isEmpty()).isEqualTo(true)
        }

    @Test
    fun `given a disabled monitored app then nothing is stored (FR-090)`() =
        runBlocking {
            apps.insert(app(BCA, enabled = false))
            val useCase = useCase(debitParser(merchant = "INDOMARET"))

            val result = useCase(BCA, "Pembayaran", "Pembayaran Rp25.000", start)

            assertThat(result).isEqualTo(ProcessNotificationResult.NotMonitored)
            assertThat(inbox.observeAll().first().isEmpty()).isEqualTo(true)
        }

    private suspend fun registerApps() {
        apps.insert(app(BCA, enabled = true))
        apps.insert(app(GOPAY, enabled = true))
    }

    private suspend fun itemById(id: InboxItemId): InboxItem =
        inbox.observeAll().first().single { it.inboxItemId == id }

    private fun useCase(vararg parsers: StubNotificationParser): ProcessNotificationUseCase =
        ProcessNotificationUseCase(
            parsers = NotificationParserRegistry(parsers.toList()),
            monitoredApps = apps,
            inbox = inbox,
            factory = DetectedItemFactory(rules, clock),
        )

    private fun app(
        packageName: String,
        enabled: Boolean,
    ): MonitoredApp =
        MonitoredApp(
            packageName = packageName,
            displayName = packageName,
            isEnabled = enabled,
            defaultWalletId = bcaDefaultWallet.takeIf { packageName == BCA },
            lastNotificationAt = null,
        )

    private fun rule(
        key: String,
        category: CategoryId,
        wallet: WalletId,
    ): MerchantRule =
        MerchantRule(
            merchantRuleId = MerchantRuleId(0),
            merchantKey = key,
            categoryId = category,
            walletId = wallet,
            hitCount = 1,
            audit = Audit(createdAt = start, updatedAt = start),
        )

    private fun debitParser(merchant: String): StubNotificationParser =
        StubNotificationParser(setOf(BCA)) { transaction(MoneyDirection.DEBIT, merchant) }

    private fun creditParser(): StubNotificationParser =
        StubNotificationParser(setOf(GOPAY)) { transaction(MoneyDirection.CREDIT, "BCA") }

    private fun ignoringParser(): StubNotificationParser =
        StubNotificationParser(setOf(BCA)) { ParseResult.Ignored(IgnoreReason.OTP) }

    private fun transaction(
        direction: MoneyDirection,
        merchant: String,
    ): ParseResult.Transaction =
        ParseResult.Transaction(
            ParsedTransaction(
                amount = Money(100_000),
                direction = direction,
                merchant = merchant,
                occurredAt = start,
                parserId = "test.stub.v1",
            ),
        )

    private companion object {
        const val BCA = "com.bca"
        const val GOPAY = "com.gojek.gopay"
    }
}
