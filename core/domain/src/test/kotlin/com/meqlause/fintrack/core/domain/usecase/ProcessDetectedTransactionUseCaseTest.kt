package com.meqlause.fintrack.core.domain.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import com.meqlause.fintrack.core.domain.detection.DetectedItemFactory
import com.meqlause.fintrack.core.domain.detection.ProcessNotificationResult
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
import com.meqlause.fintrack.core.model.ParsedTransaction
import com.meqlause.fintrack.core.model.RawNotification
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

// FR-084, FR-086, FR-088, FR-091: detected payments are stored as PENDING, deduplicated and paired

/** Process detected transaction use case tests. */
class ProcessDetectedTransactionUseCaseTest {
    private val start: Instant = Instant.parse("2026-11-23T10:00:00Z")
    private val clock: Clock = Clock.fixed(start, ZoneOffset.UTC)
    private val inbox = InMemoryInboxRepository()
    private val rules = InMemoryMerchantRuleRepository()
    private val apps = InMemoryMonitoredAppRepository()
    private val bcaDefaultWallet = WalletId(7)

    /** Given a stored payment when the same notification is reposted then it is discarded (FR-086). */
    @Test
    fun `given a stored payment when the same notification is reposted then it is discarded (FR-086)`() =
        runBlocking {
            registerApps()
            val useCase = ProcessDetectedTransactionUseCase(apps, inbox, factory())

            useCase(received(BCA_PACKAGE, "Pembayaran Rp25.000 di INDOMARET KEMANG 12"), payment(DEBIT_MERCHANT))
            val repost =
                useCase(received(BCA_PACKAGE, "Pembayaran Rp25.000 di INDOMARET KEMANG 12"), payment(DEBIT_MERCHANT))

            assertThat(repost).isEqualTo(ProcessNotificationResult.Duplicate)
            assertThat(inbox.observeAll().first().size).isEqualTo(1)
        }

    /** Given a detected payment then it is stored as PENDING with the app default wallet (FR-084, FR-082). */
    @Test
    fun `given a detected payment then it is stored as PENDING with the app default wallet (FR-084, FR-082)`() =
        runBlocking {
            registerApps()
            val useCase = ProcessDetectedTransactionUseCase(apps, inbox, factory())

            val result = useCase(received(BCA_PACKAGE, "Pembayaran"), payment(DEBIT_MERCHANT))

            assertThat(result).isInstanceOf(ProcessNotificationResult.Stored::class)
            val item = inbox.observeAll().first().single()
            assertThat(item.resolution.status).isEqualTo(InboxStatus.PENDING)
            assertThat(item.suggestion.suggestedWalletId).isEqualTo(bcaDefaultWallet)
            assertThat(item.suggestion.suggestedCategoryId).isNull()
            assertThat(item.parsed.merchant).isEqualTo(DEBIT_MERCHANT)
        }

    /** Given a remembered merchant then its category and wallet are suggested (FR-088). */
    @Test
    fun `given a remembered merchant then its category and wallet are suggested (FR-088)`() =
        runBlocking {
            registerApps()
            rules.insert(rule(ruleKey = "indomaret kemang 12", category = CategoryId(3), wallet = WalletId(9)))
            val useCase = ProcessDetectedTransactionUseCase(apps, inbox, factory())

            useCase(received(BCA_PACKAGE, "Pembayaran"), payment(DEBIT_MERCHANT))

            val item = inbox.observeAll().first().single()
            assertThat(item.suggestion.suggestedCategoryId).isEqualTo(CategoryId(3))
            assertThat(item.suggestion.suggestedWalletId).isEqualTo(WalletId(9))
        }

    /** Given a debit and a credit of one amount from two apps within 10 minutes then they are paired (FR-091). */
    @Test
    fun `given a debit and a credit of one amount from two apps within 10 minutes then they are paired (FR-091)`() =
        runBlocking {
            registerApps()
            val useCase = ProcessDetectedTransactionUseCase(apps, inbox, factory())
            val first = useCase(received(BCA_PACKAGE, "Transfer ke GoPay"), payment("GOPAY TOPUP"))
            val second =
                useCase(
                    received(GOPAY, "Transfer dari BCA", start.plusSeconds(300)),
                    payment("BCA", MoneyDirection.CREDIT, start.plusSeconds(300)),
                )

            val firstId = (first as ProcessNotificationResult.Stored).inboxItemId
            val stored = second as ProcessNotificationResult.Stored
            assertThat(stored.pairedWith).isEqualTo(firstId)
            assertThat(itemById(firstId).suggestion.relatedInboxItemId).isEqualTo(stored.inboxItemId)
            assertThat(itemById(stored.inboxItemId).suggestion.relatedInboxItemId).isEqualTo(firstId)
        }

    /** Given one amount more than 10 minutes apart then no pair is suggested (FR-091). */
    @Test
    fun `given one amount more than 10 minutes apart then no pair is suggested (FR-091)`() =
        runBlocking {
            registerApps()
            val useCase = ProcessDetectedTransactionUseCase(apps, inbox, factory())
            useCase(received(BCA_PACKAGE, "Transfer ke GoPay"), payment("GOPAY TOPUP"))
            val later =
                useCase(
                    received(GOPAY, "Transfer dari BCA", start.plusSeconds(660)),
                    payment("BCA", MoneyDirection.CREDIT, start.plusSeconds(660)),
                )

            assertThat((later as ProcessNotificationResult.Stored).pairedWith).isNull()
        }

    /** Given a notification from an app that is not monitored then nothing is stored (FR-090). */
    @Test
    fun `given a notification from an app that is not monitored then nothing is stored (FR-090)`() =
        runBlocking {
            registerApps()
            val useCase = ProcessDetectedTransactionUseCase(apps, inbox, factory())

            val result = useCase(received("com.unknown.app", "Pembayaran"), payment(DEBIT_MERCHANT))

            assertThat(result).isEqualTo(ProcessNotificationResult.NotMonitored)
            assertThat(inbox.observeAll().first().isEmpty()).isEqualTo(true)
        }

    /** Given a disabled monitored app then nothing is stored (FR-090). */
    @Test
    fun `given a disabled monitored app then nothing is stored (FR-090)`() =
        runBlocking {
            apps.insert(appOf(BCA_PACKAGE, enabled = false))
            val useCase = ProcessDetectedTransactionUseCase(apps, inbox, factory())

            val result = useCase(received(BCA_PACKAGE, "Pembayaran"), payment(DEBIT_MERCHANT))

            assertThat(result).isEqualTo(ProcessNotificationResult.NotMonitored)
            assertThat(inbox.observeAll().first().isEmpty()).isEqualTo(true)
        }

    private suspend fun registerApps() {
        apps.insert(appOf(BCA_PACKAGE, enabled = true))
        apps.insert(appOf(GOPAY, enabled = true))
    }

    private suspend fun itemById(itemId: InboxItemId): InboxItem =
        inbox.observeAll().first().single { it.inboxItemId == itemId }

    private fun factory(): DetectedItemFactory = DetectedItemFactory(rules, clock)

    private fun received(
        packageName: String,
        text: String,
        postedAt: Instant = start,
    ): RawNotification =
        RawNotification(
            packageName = packageName,
            title = "Notifikasi",
            text = text,
            bigText = "",
            postedAt = postedAt,
            notificationKey = "",
        )

    private fun payment(
        merchant: String,
        direction: MoneyDirection = MoneyDirection.DEBIT,
        occurredAt: Instant = start,
    ): ParsedTransaction =
        ParsedTransaction(
            amount = Money(100_000),
            direction = direction,
            merchant = merchant,
            merchantKey = merchant.lowercase(),
            occurredAt = occurredAt,
            parserId = "test.fixture.v1",
        )

    private fun appOf(
        packageName: String,
        enabled: Boolean,
    ): MonitoredApp =
        MonitoredApp(
            packageName = packageName,
            displayName = packageName,
            isEnabled = enabled,
            defaultWalletId = bcaDefaultWallet.takeIf { packageName == BCA_PACKAGE },
            lastNotificationAt = null,
        )

    private fun rule(
        ruleKey: String,
        category: CategoryId,
        wallet: WalletId,
    ): MerchantRule =
        MerchantRule(
            merchantRuleId = MerchantRuleId(0),
            merchantKey = ruleKey,
            categoryId = category,
            walletId = wallet,
            hitCount = 1,
            audit = Audit(createdAt = start, updatedAt = start),
        )

    private companion object {
        const val BCA_PACKAGE = "com.bca"
        const val GOPAY = "com.gojek.gopay"
        const val DEBIT_MERCHANT = "INDOMARET KEMANG 12"
    }
}
