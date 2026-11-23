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
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

private const val RESOLVED_DAYS = 7L
private val RESOLVED_WINDOW: Duration = Duration.ofDays(RESOLVED_DAYS)

// FR-084, FR-091, S-07: the stored data the screen is built from; hasAccess is the notification listener grant
internal data class InboxSnapshot(
    val items: List<InboxItem>,
    val wallets: List<Wallet>,
    val categories: List<Category>,
    val hasAccess: Boolean,
)

// S-07: the display names of the wallets and the categories, looked up by id
private data class InboxNames(
    val wallets: Map<Long, String>,
    val categories: Map<Long, String>,
)

// FR-084, FR-091, S-07: turns the stored items into the screen state; a pair shows once, from its DEBIT side
internal fun buildInboxState(
    snapshot: InboxSnapshot,
    local: InboxLocal,
    clock: Clock,
): InboxUiState {
    val names =
        InboxNames(
            wallets = snapshot.wallets.associate { it.walletId.value to it.profile.name },
            categories = snapshot.categories.associate { it.categoryId.value to it.profile.name },
        )
    val pending =
        snapshot.items
            .filter { it.resolution.status == InboxStatus.PENDING }
            .sortedByDescending { it.notice.postedAt }
    return InboxUiState(
        isLoaded = true,
        hasAccess = snapshot.hasAccess,
        pending = pendingOf(pending, names, local, clock.zone),
        resolved = resolvedRows(snapshot.items, Instant.now(clock)),
        options = optionsState(snapshot),
        undoItemId = local.undoItemId,
    )
}

// FR-087, FR-091: the cards that stand alone and the pairs, after the user's edits and split choices
private fun pendingOf(
    pending: List<InboxItem>,
    names: InboxNames,
    local: InboxLocal,
    zone: ZoneId,
): InboxPending {
    val pairs = pairsOf(pending, local.separated)
    val pairedIds = pairs.flatMap { listOf(it.first.inboxItemId.value, it.second.inboxItemId.value) }.toSet()
    val cards =
        pending
            .filter { it.inboxItemId.value !in pairedIds }
            .map { card(it, names, local.edits[it.inboxItemId.value], zone) }
    return InboxPending(
        cards = cards,
        pairs = pairs.map { pairCard(it.first, it.second, names, local.edits) },
    )
}

// FR-091: a pair is a debit and a credit linked to each other, both still pending and not split by the user
private fun pairsOf(
    pending: List<InboxItem>,
    separated: Set<Long>,
): List<Pair<InboxItem, InboxItem>> {
    val byId = pending.associateBy { it.inboxItemId.value }
    return pending.mapNotNull { item ->
        val partner =
            item.suggestion.relatedInboxItemId
                ?.value
                ?.let { byId[it] } ?: return@mapNotNull null
        val linked = item.inboxItemId.value !in separated && partner.inboxItemId.value !in separated
        val isPair = linked && partner.parsed.direction != item.parsed.direction
        if (isPair && item.parsed.direction == MoneyDirection.DEBIT) item to partner else null
    }
}

// FR-087: the edit made on the card wins over the suggestion (FR-088, FR-082)
private fun card(
    item: InboxItem,
    names: InboxNames,
    edit: InboxEdit?,
    zone: ZoneId,
): InboxCardUi {
    val walletId = edit?.walletId ?: item.suggestion.suggestedWalletId?.value
    val categoryId = edit?.categoryId ?: item.suggestion.suggestedCategoryId?.value
    val time =
        item.notice.postedAt
            .atZone(zone)
            .toLocalTime()
    return InboxCardUi(
        itemId = item.inboxItemId.value,
        text =
            InboxCardText(
                sourceLabel = InboxSourceLabel.of(item.notice.packageName),
                timeLabel = DateFormatter.timeLabel(time),
                merchant = item.parsed.merchant,
            ),
        amount = item.parsed.amount,
        type = typeOf(item),
        wallet = InboxSlot(walletId, walletId?.let { names.wallets[it] }),
        category = InboxSlot(categoryId, categoryId?.let { names.categories[it] }),
    )
}

// FR-091: the wallets of a pair, with the edits the user made on either side
private fun pairCard(
    from: InboxItem,
    toItem: InboxItem,
    names: InboxNames,
    edits: Map<Long, InboxEdit>,
): InboxPairUi {
    val fromWallet = edits[from.inboxItemId.value]?.walletId ?: from.suggestion.suggestedWalletId?.value
    val toWallet = edits[toItem.inboxItemId.value]?.walletId ?: toItem.suggestion.suggestedWalletId?.value
    return InboxPairUi(
        fromItemId = from.inboxItemId.value,
        toItemId = toItem.inboxItemId.value,
        from = InboxSlot(fromWallet, fromWallet?.let { names.wallets[it] }),
        target = InboxSlot(toWallet, toWallet?.let { names.wallets[it] }),
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

// S-18: the wallet and category choices of the picker drawers
private fun optionsState(snapshot: InboxSnapshot): InboxOptions =
    InboxOptions(
        wallets = snapshot.wallets.map { PickerOption(it.walletId.value.toString(), it.profile.name) },
        expenses = optionsOf(snapshot.categories, CategoryKind.EXPENSE),
        incomes = optionsOf(snapshot.categories, CategoryKind.INCOME),
    )

// S-18: category choices of one kind, in the order the user keeps them
private fun optionsOf(
    categories: List<Category>,
    kind: CategoryKind,
): List<PickerOption> =
    categories
        .filter { it.profile.kind == kind }
        .sortedBy { it.profile.sortOrder }
        .map { PickerOption(it.categoryId.value.toString(), it.profile.name) }
