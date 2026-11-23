package com.meqlause.fintrack.feature.inbox

import com.meqlause.fintrack.core.model.Category
import com.meqlause.fintrack.core.model.CategoryKind
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.MoneyDirection
import com.meqlause.fintrack.core.model.TransactionType
import com.meqlause.fintrack.core.model.Wallet
import com.meqlause.fintrack.core.ui.format.DateFormatter
import com.meqlause.fintrack.core.ui.inbox.InboxSourceLabel
import com.meqlause.fintrack.core.ui.picker.PickerOption
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

private val RESOLVED_WINDOW: Duration = Duration.ofDays(7L)

// FR-084, FR-091, S-07: turns the stored items into the screen state; a pair shows once, from its DEBIT side
internal fun buildInboxState(
    items: List<InboxItem>,
    wallets: List<Wallet>,
    categories: List<Category>,
    local: InboxLocal,
    hasAccess: Boolean,
    currentTime: Instant,
    zone: ZoneId,
): InboxUiState {
    val walletNames = wallets.associate { it.walletId.value to it.profile.name }
    val categoryNames = categories.associate { it.categoryId.value to it.profile.name }
    val pending = items.filter { it.resolution.status == InboxStatus.PENDING }.sortedByDescending { it.notice.postedAt }
    val byId = pending.associateBy { it.inboxItemId.value }
    val pairs = pairsOf(pending, byId, local.separated)
    val pairedIds = pairs.flatMap { listOf(it.first.inboxItemId.value, it.second.inboxItemId.value) }.toSet()
    val cards =
        pending
            .filter { it.inboxItemId.value !in pairedIds }
            .map { card(it, walletNames, categoryNames, local.edits[it.inboxItemId.value], zone) }
    return InboxUiState(
        isLoaded = true,
        hasAccess = hasAccess,
        cards = cards,
        pairs = pairs.map { pairCard(it.first, it.second, walletNames, local.edits) },
        resolved = resolvedRows(items, currentTime),
        walletOptions = wallets.map { PickerOption(it.walletId.value.toString(), it.profile.name) },
        expenseOptions = optionsOf(categories, CategoryKind.EXPENSE),
        incomeOptions = optionsOf(categories, CategoryKind.INCOME),
        undoItemId = local.undoItemId,
    )
}

// FR-091: a pair is a debit and a credit linked to each other, both still pending and not split by the user
private fun pairsOf(
    pending: List<InboxItem>,
    byId: Map<Long, InboxItem>,
    separated: Set<Long>,
): List<Pair<InboxItem, InboxItem>> =
    pending.mapNotNull { item ->
        val partner =
            item.suggestion.relatedInboxItemId
                ?.value
                ?.let { byId[it] } ?: return@mapNotNull null
        val linked = item.inboxItemId.value !in separated && partner.inboxItemId.value !in separated
        val opposite = item.parsed.direction != partner.parsed.direction
        val isFromSide = item.parsed.direction == MoneyDirection.DEBIT
        if (linked && opposite && isFromSide) item to partner else null
    }

// FR-087: the edit made on the card wins over the suggestion (FR-088, FR-082)
private fun card(
    item: InboxItem,
    walletNames: Map<Long, String>,
    categoryNames: Map<Long, String>,
    edit: InboxEdit?,
    zone: ZoneId,
): InboxCardUi {
    val walletId = edit?.walletId ?: item.suggestion.suggestedWalletId?.value
    val categoryId = edit?.categoryId ?: item.suggestion.suggestedCategoryId?.value
    return InboxCardUi(
        itemId = item.inboxItemId.value,
        sourceLabel = InboxSourceLabel.of(item.notice.packageName),
        timeLabel =
            DateFormatter.timeLabel(
                item.notice.postedAt
                    .atZone(zone)
                    .toLocalTime(),
            ),
        amount = item.parsed.amount,
        type = typeOf(item),
        merchant = item.parsed.merchant,
        walletId = walletId,
        walletName = walletId?.let { walletNames[it] },
        categoryId = categoryId,
        categoryName = categoryId?.let { categoryNames[it] },
    )
}

private fun pairCard(
    from: InboxItem,
    toItem: InboxItem,
    walletNames: Map<Long, String>,
    edits: Map<Long, InboxEdit>,
): InboxPairUi {
    val fromWallet = edits[from.inboxItemId.value]?.walletId ?: from.suggestion.suggestedWalletId?.value
    val toWallet = edits[toItem.inboxItemId.value]?.walletId ?: toItem.suggestion.suggestedWalletId?.value
    return InboxPairUi(
        fromItemId = from.inboxItemId.value,
        toItemId = toItem.inboxItemId.value,
        fromWalletId = fromWallet,
        fromWalletName = fromWallet?.let { walletNames[it] },
        toWalletId = toWallet,
        toWalletName = toWallet?.let { walletNames[it] },
        amount = from.parsed.amount,
    )
}

// S-07 "Selesai": confirmed and dismissed items of the last seven days, newest first
private fun resolvedRows(
    items: List<InboxItem>,
    currentTime: Instant,
): List<InboxResolvedUi> {
    val since = currentTime.minus(RESOLVED_WINDOW)
    return items
        .filter { it.resolution.status == InboxStatus.CONFIRMED || it.resolution.status == InboxStatus.DISMISSED }
        .filter { (it.resolution.resolvedAt ?: since) >= since }
        .sortedByDescending { it.resolution.resolvedAt }
        .map { item ->
            InboxResolvedUi(
                itemId = item.inboxItemId.value,
                label = item.parsed.merchant.ifEmpty { InboxSourceLabel.of(item.notice.packageName) },
                amount = item.parsed.amount,
                type = typeOf(item),
                isConfirmed = item.resolution.status == InboxStatus.CONFIRMED,
            )
        }
}

// BR-013: a DEBIT item is an expense and a CREDIT item an income
private fun typeOf(item: InboxItem): TransactionType =
    if (item.parsed.direction == MoneyDirection.DEBIT) TransactionType.EXPENSE else TransactionType.INCOME

// S-18: category choices of one kind, in the order the user keeps them
private fun optionsOf(
    categories: List<Category>,
    kind: CategoryKind,
): List<PickerOption> =
    categories
        .filter { it.profile.kind == kind }
        .sortedBy { it.profile.sortOrder }
        .map { PickerOption(it.categoryId.value.toString(), it.profile.name) }
