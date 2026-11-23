package com.meqlause.fintrack.core.domain.usecase

import com.meqlause.fintrack.core.domain.inbox.ConfirmInboxResult
import com.meqlause.fintrack.core.domain.inbox.InboxChoice
import com.meqlause.fintrack.core.domain.repository.InboxRepository
import com.meqlause.fintrack.core.model.InboxItemId
import javax.inject.Inject

/** FR-089: confirms an item with the wallet and category it was suggested, used by the heads-up prompt. */
public class ConfirmSuggestedInboxItemUseCase
    @Inject
    constructor(
        private val inbox: InboxRepository,
        private val confirm: ConfirmInboxItemUseCase,
    ) {
        /** FR-089: without a suggested wallet or category nothing is stored; the Konfirmasi action is hidden then. */
        public suspend operator fun invoke(inboxItemId: InboxItemId): ConfirmInboxResult {
            val item = inbox.findById(inboxItemId) ?: return ConfirmInboxResult.NotPending
            val suggestion = item.suggestion
            return confirm(InboxChoice(inboxItemId, suggestion.suggestedWalletId, suggestion.suggestedCategoryId))
        }
    }
