package com.meqlause.fintrack.core.data.mapper

import com.meqlause.fintrack.core.database.entity.InboxItemEntity
import com.meqlause.fintrack.core.model.CategoryId
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.Money
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Suggestion
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId

// DOC-02, FR-090, FR-091: maps an inbox row to the domain model
internal fun InboxItemEntity.toInboxItem(): InboxItem =
    InboxItem(
        inboxItemId = InboxItemId(inboxItemId),
        notice = toNotice(),
        parsed = ParsedAmount(amount = Money(parsed.amount), direction = parsed.direction, merchant = parsed.merchant),
        suggestion = toSuggestion(),
        resolution =
            Resolution(
                status = lifecycle.status,
                transactionId = lifecycle.transactionId?.let(::TransactionId),
                resolvedAt = lifecycle.resolvedAt?.toInstant(),
            ),
        audit = audit.toAudit(),
    )

// DOC-02, FR-090: the notification as received, with its fingerprint (FR-092)
private fun InboxItemEntity.toNotice(): RawNotice =
    RawNotice(
        packageName = source.packageName,
        parserId = source.parserId,
        rawTitle = source.rawTitle,
        rawText = source.rawText,
        postedAt = source.postedAt.toInstant(),
        fingerprint = lifecycle.fingerprint,
    )

// DOC-02, FR-091: the wallet and category suggested for the item, and its possible pair
private fun InboxItemEntity.toSuggestion(): Suggestion =
    Suggestion(
        suggestedWalletId = suggestion.suggestedWalletId?.let(::WalletId),
        suggestedCategoryId = suggestion.suggestedCategoryId?.let(::CategoryId),
        relatedInboxItemId = lifecycle.relatedInboxItemId?.let(::InboxItemId),
    )

// DOC-02, FR-090, FR-091: maps an inbox item to its row
internal fun InboxItem.toEntity(): InboxItemEntity =
    InboxItemEntity(
        inboxItemId = inboxItemId.value,
        source =
            InboxItemEntity.Source(
                packageName = notice.packageName,
                parserId = notice.parserId,
                postedAt = notice.postedAt.toEpochMilli(),
                rawTitle = notice.rawTitle,
                rawText = notice.rawText,
            ),
        parsed =
            InboxItemEntity.Parsed(
                amount = parsed.amount.rupiah,
                direction = parsed.direction,
                merchant = parsed.merchant,
            ),
        suggestion = toSuggestionColumns(),
        lifecycle = toLifecycleColumns(),
        audit = audit.toColumns(),
    )

// DOC-02, FR-091: suggestion columns of an inbox row
private fun InboxItem.toSuggestionColumns(): InboxItemEntity.Suggestion =
    InboxItemEntity.Suggestion(
        suggestedWalletId = suggestion.suggestedWalletId?.value,
        suggestedCategoryId = suggestion.suggestedCategoryId?.value,
    )

// DOC-02, FR-092: review state, dedup key and the transaction created from the item
private fun InboxItem.toLifecycleColumns(): InboxItemEntity.Lifecycle =
    InboxItemEntity.Lifecycle(
        status = resolution.status,
        fingerprint = notice.fingerprint,
        relatedInboxItemId = suggestion.relatedInboxItemId?.value,
        transactionId = resolution.transactionId?.value,
        resolvedAt = resolution.resolvedAt?.toEpochMilli(),
    )
