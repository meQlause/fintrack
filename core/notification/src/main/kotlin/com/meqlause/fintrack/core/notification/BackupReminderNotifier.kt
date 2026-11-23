package com.meqlause.fintrack.core.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val BACKUP_CHANNEL_ID = "backup_reminder"
private const val BACKUP_TAG = "backup_reminder"
private const val BACKUP_NOTIFICATION_ID = 1

// FR-112: posts the backup reminder on its own channel
@Singleton
public class BackupReminderNotifier
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        /** FR-112: shows "Sudah N hari sejak cadangan terakhir"; tapping it opens the app. */
        public fun show(days: Int) {
            if (!canPostNotifications(context)) return
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            createChannel(manager)
            val builder =
                Notification
                    .Builder(context, BACKUP_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_inbox_notification)
                    .setContentTitle(context.getString(R.string.backup_reminder_title))
                    .setContentText(context.getString(R.string.backup_reminder_text, days))
                    .setContentIntent(openAppIntent(context))
                    .setAutoCancel(true)
            manager.notify(BACKUP_TAG, BACKUP_NOTIFICATION_ID, builder.build())
        }

        private fun createChannel(manager: NotificationManager) {
            val channel =
                NotificationChannel(
                    BACKUP_CHANNEL_ID,
                    context.getString(R.string.backup_reminder_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            manager.createNotificationChannel(channel)
        }
    }
