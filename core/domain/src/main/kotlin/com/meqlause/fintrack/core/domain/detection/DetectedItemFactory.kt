package com.meqlause.fintrack.core.domain.detection

import com.meqlause.fintrack.core.domain.repository.MerchantRuleRepository
import com.meqlause.fintrack.core.model.Audit
import com.meqlause.fintrack.core.model.InboxItem
import com.meqlause.fintrack.core.model.InboxItemId
import com.meqlause.fintrack.core.model.InboxStatus
import com.meqlause.fintrack.core.model.MerchantRule
import com.meqlause.fintrack.core.model.MonitoredApp
import com.meqlause.fintrack.core.model.ParsedAmount
import com.meqlause.fintrack.core.model.ParsedTransaction
import com.meqlause.fintrack.core.model.RawNotice
import com.meqlause.fintrack.core.model.RawNotification
import com.meqlause.fintrack.core.model.Resolution
import com.meqlause.fintrack.core.model.Suggestion
import java.time.Clock
import javax.inject.Inject

/** FR-084, FR-088, FR-082: builds a PENDING inbox item with its wallet and category suggestion. */
public class DetectedItemFactory
    @Inject
    constructor(
        private val merchantRules: MerchantRuleRepository,
        private val clock: Clock,
    ) {
        /** FR-084: the item is PENDING until the user confirms it; nothing is recorded before that. */
        public suspend fun build(
            rawNotification: RawNotification,
            parsed: ParsedTransaction,
            monitored: MonitoredApp,
            fingerprint: String,
        ): InboxItem {
            val now = clock.instant()
            val rule = suggestedRule(parsed.merchantKey)
            return InboxItem(
                inboxItemId = InboxItemId(0),
                notice = notice(rawNotification, parsed, fingerprint),
                parsed = ParsedAmount(parsed.amount, parsed.direction, parsed.merchant),
                suggestion = suggestion(rule, monitored),
                resolution = Resolution(InboxStatus.PENDING, transactionId = null, resolvedAt = null),
                audit = Audit(createdAt = now, updatedAt = now),
            )
        }

        // FR-088: merchant memory is consulted only for a merchant that has a usable key
        private suspend fun suggestedRule(merchantKey: String) =
            merchantKey.takeIf { it.isNotEmpty() }?.let { merchantRules.findByKey(it) }

        // FR-082: the rule's wallet wins over the monitored app's default wallet
        private fun suggestion(
            rule: MerchantRule?,
            monitored: MonitoredApp,
        ): Suggestion =
            Suggestion(
                suggestedWalletId = rule?.walletId ?: monitored.defaultWalletId,
                suggestedCategoryId = rule?.categoryId,
                relatedInboxItemId = null,
            )

        private fun notice(
            rawNotification: RawNotification,
            parsed: ParsedTransaction,
            fingerprint: String,
        ): RawNotice =
            RawNotice(
                packageName = rawNotification.packageName,
                parserId = parsed.parserId,
                rawTitle = rawNotification.title,
                rawText = rawNotification.bigText.ifEmpty { rawNotification.text },
                postedAt = rawNotification.postedAt,
                fingerprint = fingerprint,
            )
    }
