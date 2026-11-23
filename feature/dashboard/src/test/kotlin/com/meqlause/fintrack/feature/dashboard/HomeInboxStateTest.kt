package com.meqlause.fintrack.feature.dashboard

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
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
import com.meqlause.fintrack.core.testing.repository.FakeTransactionRepository
import com.meqlause.fintrack.core.testing.repository.FakeWalletRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

// FR-084, S-03: only the notices still pending count on the Beranda inbox banner and its source badges
@OptIn(ExperimentalCoroutinesApi::class)
class HomeInboxStateTest : HomeViewModelTestBase() {
    // FR-084: resolved notices are left out of the count, and the badges name each distinct source once
    @Test
    fun givenPendingAndResolvedNoticesWhenOpenedThenOnlyPendingAreCounted() =
        runTest {
            inboxItems.insert(item("com.bca.mobile", 1L, InboxStatus.PENDING))
            inboxItems.insert(item("com.bca.mobile", 2L, InboxStatus.PENDING))
            inboxItems.insert(item("com.gojek.app", 3L, InboxStatus.PENDING))
            inboxItems.insert(item("ovo.id", 4L, InboxStatus.CONFIRMED))
            val viewModel = viewModel(FakeWalletRepository(), FakeTransactionRepository())
            val collector = backgroundScope.launch { viewModel.state.collect {} }
            advanceUntilIdle()

            val inboxState = (viewModel.state.value as HomeUiState.Success).inbox
            assertThat(inboxState.count).isEqualTo(3)
            assertThat(inboxState.sources).containsExactly("BCA", "GoPay")
            collector.cancel()
        }

    private fun item(
        packageName: String,
        itemKey: Long,
        status: InboxStatus,
    ): InboxItem =
        InboxItem(
            inboxItemId = InboxItemId(itemKey),
            notice =
                RawNotice(
                    packageName = packageName,
                    parserId = "bca-qris",
                    rawTitle = "Transaksi",
                    rawText = "Rp 10.000",
                    postedAt = fixedNow,
                    fingerprint = "fp-$itemKey",
                ),
            parsed = ParsedAmount(amount = Money(10_000L), direction = MoneyDirection.DEBIT, merchant = "Toko"),
            suggestion = Suggestion(suggestedWalletId = null, suggestedCategoryId = null, relatedInboxItemId = null),
            resolution = Resolution(status = status, transactionId = null, resolvedAt = Instant.EPOCH),
            audit = Audit(createdAt = fixedNow, updatedAt = fixedNow),
        )
}
