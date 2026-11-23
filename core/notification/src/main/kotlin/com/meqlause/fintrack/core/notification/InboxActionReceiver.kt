package com.meqlause.fintrack.core.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meqlause.fintrack.core.common.coroutines.ApplicationScope
import com.meqlause.fintrack.core.domain.usecase.ConfirmSuggestedInboxItemUseCase
import com.meqlause.fintrack.core.domain.usecase.DismissInboxItemUseCase
import com.meqlause.fintrack.core.model.InboxItemId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// FR-089: handles Konfirmasi and Abaikan of the heads-up prompt without opening the app
@AndroidEntryPoint
public class InboxActionReceiver : BroadcastReceiver() {
    @Inject public lateinit var confirmSuggested: ConfirmSuggestedInboxItemUseCase

    @Inject public lateinit var dismiss: DismissInboxItemUseCase

    @Inject @ApplicationScope
    public lateinit var appScope: CoroutineScope

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val itemId = InboxItemId(intent.getLongExtra(EXTRA_ITEM_ID, 0L))
        val pending = goAsync()
        appScope.launch(Dispatchers.Default) {
            try {
                act(intent.action, itemId)
            } finally {
                context
                    .getSystemService(NotificationManager::class.java)
                    ?.cancel(INBOX_NOTIFICATION_TAG, itemId.value.toInt())
                pending.finish()
            }
        }
    }

    private suspend fun act(
        action: String?,
        itemId: InboxItemId,
    ) {
        when (action) {
            ACTION_CONFIRM -> confirmSuggested(itemId)
            ACTION_DISMISS -> dismiss(itemId)
        }
    }

    public companion object {
        public const val ACTION_CONFIRM: String = "com.meqlause.fintrack.action.INBOX_CONFIRM"
        public const val ACTION_DISMISS: String = "com.meqlause.fintrack.action.INBOX_DISMISS"
        public const val EXTRA_ITEM_ID: String = "inboxItemId"
    }
}
