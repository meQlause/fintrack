package com.meqlause.fintrack.core.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import com.meqlause.fintrack.core.model.InboxItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

internal const val INBOX_NOTIFICATION_TAG = "inbox"
private const val INBOX_CHANNEL_ID = "inbox"
private const val FLAGS = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

// FR-089: optional heads-up prompt from our app with Konfirmasi and Abaikan actions; nothing leaves the device
@Singleton
public class InboxNotifier
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        /** FR-089: shows "Rp25.000 di Indomaret — Konfirmasi?"; nothing is posted without the permission. */
        public fun showConfirmPrompt(item: InboxItem) {
            if (!mayPost()) return
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            createChannel(manager)
            val builder =
                Notification
                    .Builder(context, INBOX_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_inbox_notification)
                    .setContentTitle(title(item))
                    .setContentText(context.getString(R.string.inbox_prompt_text))
                    .setContentIntent(openAppIntent())
                    .setAutoCancel(true)
            if (item.suggestion.suggestedWalletId != null && item.suggestion.suggestedCategoryId != null) {
                builder.addAction(action(item, InboxActionReceiver.ACTION_CONFIRM, R.string.inbox_action_confirm, 0))
            }
            builder.addAction(action(item, InboxActionReceiver.ACTION_DISMISS, R.string.inbox_action_dismiss, 1))
            manager.notify(INBOX_NOTIFICATION_TAG, item.inboxItemId.value.toInt(), builder.build())
        }

        // FR-089: the permission is a runtime grant on Android 13 and later
        private fun mayPost(): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        private fun createChannel(manager: NotificationManager) {
            val channel =
                NotificationChannel(
                    INBOX_CHANNEL_ID,
                    context.getString(R.string.inbox_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            manager.createNotificationChannel(channel)
        }

        private fun title(item: InboxItem): String {
            val amount = rupiah(item.parsed.amount.rupiah)
            val merchant = item.parsed.merchant
            return if (merchant.isEmpty()) {
                context.getString(R.string.inbox_prompt_title_unknown, amount)
            } else {
                context.getString(R.string.inbox_prompt_title, amount, merchant)
            }
        }

        private fun action(
            item: InboxItem,
            actionName: String,
            labelRes: Int,
            slot: Int,
        ): Notification.Action {
            val requestCode = item.inboxItemId.value.toInt() * 2 + slot
            val intent =
                Intent(context, InboxActionReceiver::class.java)
                    .setAction(actionName)
                    .putExtra(InboxActionReceiver.EXTRA_ITEM_ID, item.inboxItemId.value)
            val pending =
                PendingIntent.getBroadcast(context, requestCode, intent, FLAGS)
            val icon = Icon.createWithResource(context, R.drawable.ic_inbox_notification)
            return Notification.Action.Builder(icon, context.getString(labelRes), pending).build()
        }

        private fun openAppIntent(): PendingIntent? {
            val launch = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return null
            return PendingIntent.getActivity(context, 0, launch, PendingIntent.FLAG_IMMUTABLE)
        }

        // BR-009 display only: amounts are whole rupiah with dot grouping, as in Indonesian copy
        private fun rupiah(amount: Long): String =
            "Rp" +
                amount
                    .toString()
                    .reversed()
                    .chunked(3)
                    .joinToString(".")
                    .reversed()
    }
