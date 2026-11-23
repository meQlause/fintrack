package com.meqlause.fintrack.feature.inbox

import com.meqlause.fintrack.core.domain.detection.NotificationAccess
import com.meqlause.fintrack.core.domain.usecase.ConfirmInboxItemUseCase
import com.meqlause.fintrack.core.domain.usecase.DismissAllInboxItemsUseCase
import com.meqlause.fintrack.core.domain.usecase.DismissInboxItemUseCase
import com.meqlause.fintrack.core.domain.usecase.RestoreInboxItemUseCase
import com.meqlause.fintrack.core.model.Appearance
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.CategoryProfile
import com.meqlause.fintrack.core.model.CategoryState
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.model.WalletId
import com.meqlause.fintrack.core.model.WalletKind
import com.meqlause.fintrack.core.model.WalletProfile
import com.meqlause.fintrack.core.model.WalletStatus
import com.meqlause.fintrack.core.model.WalletTerms
import com.meqlause.fintrack.core.model.WalletType
import com.meqlause.fintrack.core.testing.repository.FakeCategoryRepository
import com.meqlause.fintrack.core.testing.repository.FakeInboxRepository
import com.meqlause.fintrack.core.testing.repository.FakeMerchantRuleRepository
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import com.meqlause.fintrack.core.testing.repository.PassthroughAtomicRunner
import com.meqlause.fintrack.core.testing.time.FakeClock
import java.time.Instant

// FR-087, S-07: the fakes and builders the Kotak Masuk tests share
internal class InboxTestFixtures {
    val stamp: Instant = Instant.parse("2026-11-23T07:05:00Z")
    val clock = FakeClock(stamp)
    val inbox = FakeInboxRepository()
    val transactions = FakeTransactionRepository()
    val wallets = FakeWalletRepository(transactions)
    val categories = FakeCategoryRepository()
    var granted = true

    fun viewModel(): InboxViewModel {
        val atomic = PassthroughAtomicRunner()
        val commands =
            InboxCommands(
                confirmItem = ConfirmInboxItemUseCase(inbox, transactions, FakeMerchantRuleRepository(), atomic, clock),
                dismissItem = DismissInboxItemUseCase(inbox, clock),
                dismissAllItems = DismissAllInboxItemsUseCase(inbox, atomic, clock),
                restoreItem = RestoreInboxItemUseCase(inbox),
            )
        val access =
            object : NotificationAccess {
                override fun isGranted(): Boolean = granted
            }
        return InboxViewModel(inbox, wallets, categories, access, commands, clock)
    }

    suspend fun wallet(name: String): WalletId =
        wallets.insert(
            Wallet(
                walletId = WalletId(0L),
                kind = WalletKind(WalletType.BANK, investKind = null),
                terms = WalletTerms(Money(1_000_000L), credit = null),
                profile = WalletProfile(name, Appearance("bank", "blue"), sortOrder = 0),
                status = WalletStatus(),
                audit = Audit(Instant.EPOCH, Instant.EPOCH),
            ),
        )

    suspend fun category(
        name: String,
        kind: CategoryKind,
        order: Int,
    ): CategoryId =
        categories.insert(
            Category(
                categoryId = CategoryId(0L),
                profile = CategoryProfile(name, kind, Appearance("food", "orange"), order),
                state = CategoryState(isDefault = false, isArchived = false),
                audit = Audit(Instant.EPOCH, Instant.EPOCH),
            ),
        )

    fun item(
        direction: MoneyDirection,
        merchant: String = "Warung",
        suggestion: Suggestion = Suggestion(null, null, null),
        resolution: Resolution = Resolution(InboxStatus.PENDING, null, null),
    ): InboxItem =
        InboxItem(
            inboxItemId = InboxItemId(0L),
            notice = RawNotice("com.bca", "bca", "Judul", "Rp25.000", stamp, "fp-${inboxCounter++}"),
            parsed = ParsedAmount(Money(25_000L), direction, merchant),
            suggestion = suggestion,
            resolution = resolution,
            audit = Audit(stamp, stamp),
        )

    var inboxCounter = 0

    fun gojekItem(): InboxItem =
        item(MoneyDirection.DEBIT).let { it.copy(notice = it.notice.copy(packageName = "com.gojek.app")) }

    suspend fun pairOf(
        from: WalletId,
        target: WalletId,
    ): Pair<Long, Long> {
        val debit = inbox.insert(item(MoneyDirection.DEBIT, suggestion = Suggestion(from, null, InboxItemId(2L))))
        val credit = inbox.insert(item(MoneyDirection.CREDIT, suggestion = Suggestion(target, null, debit)))
        return debit.value to credit.value
    }

    suspend fun insertResolved(
        direction: MoneyDirection,
        merchant: String,
        status: InboxStatus,
        resolvedAt: Instant,
    ) {
        inbox.insert(item(direction, merchant, resolution = Resolution(status, null, resolvedAt)))
    }
}
