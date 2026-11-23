package com.meqlause.fintrack.core.backup

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
import com.meqlause.fintrack.core.model.TransactionId
import com.meqlause.fintrack.core.model.WalletId
import kotlinx.serialization.Serializable

// DOC-11: one pending inbox item in the backup, raw notification text included
@Serializable
internal data class InboxItemDto(
    val inboxItemId: Long,
    val notice: NoticeDto,
    val parsed: ParsedDto,
    val suggestion: SuggestionDto,
    val resolution: ResolutionDto,
    val audit: AuditDto,
) {
    fun toModel(): InboxItem =
        InboxItem(
            inboxItemId = InboxItemId(inboxItemId),
            notice = noticeModel(),
            parsed =
                ParsedAmount(
                    Money(parsed.amount),
                    BackupValues.enumNamed<MoneyDirection>(parsed.direction),
                    parsed.merchant,
                ),
            suggestion =
                Suggestion(
                    suggested(suggestion.suggestedWalletId, ::WalletId),
                    suggested(suggestion.suggestedCategoryId, ::CategoryId),
                    suggested(suggestion.relatedInboxItemId, ::InboxItemId),
                ),
            resolution =
                Resolution(
                    BackupValues.enumNamed<InboxStatus>(resolution.status),
                    resolution.transactionId?.let(::TransactionId),
                    resolution.resolvedAt?.let(BackupValues::instant),
                ),
            audit = audit.toModel(),
        )

    private fun noticeModel(): RawNotice =
        RawNotice(
            notice.packageName,
            notice.parserId,
            notice.rawTitle,
            notice.rawText,
            BackupValues.instant(notice.postedAt),
            notice.fingerprint,
        )

    companion object {
        fun from(item: InboxItem): InboxItemDto =
            InboxItemDto(
                inboxItemId = item.inboxItemId.value,
                notice =
                    NoticeDto(
                        item.notice.packageName,
                        item.notice.parserId,
                        item.notice.rawTitle,
                        item.notice.rawText,
                        item.notice.postedAt.toString(),
                        item.notice.fingerprint,
                    ),
                parsed = ParsedDto(item.parsed.amount.rupiah, item.parsed.direction.name, item.parsed.merchant),
                suggestion =
                    SuggestionDto(
                        item.suggestion.suggestedWalletId?.value,
                        item.suggestion.suggestedCategoryId?.value,
                        item.suggestion.relatedInboxItemId?.value,
                    ),
                resolution =
                    ResolutionDto(
                        item.resolution.status.name,
                        item.resolution.transactionId?.value,
                        item.resolution.resolvedAt?.toString(),
                    ),
                audit = AuditDto.from(item.audit),
            )

        private fun <T, R> suggested(
            value: T?,
            wrap: (T) -> R,
        ): R? = value?.let(wrap)
    }

    @Serializable
    internal data class NoticeDto(
        val packageName: String,
        val parserId: String,
        val rawTitle: String,
        val rawText: String,
        val postedAt: String,
        val fingerprint: String,
    )

    @Serializable
    internal data class ParsedDto(
        val amount: Long,
        val direction: String,
        val merchant: String,
    )

    @Serializable
    internal data class SuggestionDto(
        val suggestedWalletId: Long?,
        val suggestedCategoryId: Long?,
        val relatedInboxItemId: Long?,
    )

    @Serializable
    internal data class ResolutionDto(
        val status: String,
        val transactionId: Long?,
        val resolvedAt: String?,
    )
}
