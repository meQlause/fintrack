package com.meqlause.fintrack.feature.inbox

import com.meqlause.fintrack.core.domain.inbox.InboxChoice
import com.meqlause.fintrack.core.domain.usecase.ConfirmInboxItemUseCase
import com.meqlause.fintrack.core.domain.usecase.DismissAllInboxItemsUseCase
import com.meqlause.fintrack.core.domain.usecase.DismissInboxItemUseCase
import com.meqlause.fintrack.core.domain.usecase.RestoreInboxItemUseCase
import com.meqlause.fintrack.core.model.InboxItemId
import javax.inject.Inject

// FR-087, FR-091: the use cases that store the user's decisions on inbox items
internal class InboxCommands
    @Inject
    constructor(
        private val confirmItem: ConfirmInboxItemUseCase,
        private val dismissItem: DismissInboxItemUseCase,
        private val dismissAllItems: DismissAllInboxItemsUseCase,
        private val restoreItem: RestoreInboxItemUseCase,
    ) {
        // FR-087, FR-091: Catat stores the chosen wallet, category and transfer target
        suspend fun confirm(choice: InboxChoice) = confirmItem(choice)

        // FR-087: Abaikan marks the item as dismissed
        suspend fun dismiss(itemId: InboxItemId) = dismissItem(itemId)

        // S-07 overflow: Abaikan semua
        suspend fun dismissAll() = dismissAllItems()

        // FR-087: Batalkan puts a dismissed item back to pending
        suspend fun restore(itemId: InboxItemId) = restoreItem(itemId)
    }
