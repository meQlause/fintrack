package com.meqlause.fintrack.core.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.meqlause.fintrack.core.domain.budget.BudgetAlert
import com.meqlause.fintrack.core.domain.budget.BudgetRules
import com.meqlause.fintrack.core.domain.port.BudgetAlertNotifier
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

internal const val BUDGET_NOTIFICATION_TAG = "budget"
private const val BUDGET_CHANNEL_ID = "budget_alert"

// FR-073: local alert when a budget crosses 80 or 100 percent; the channel is default importance (docs/07)

/** FR-073: posts the local notification when a budget crosses 80 % or 100 %. */
@Singleton
public class BudgetNotifier
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : BudgetAlertNotifier {
        /** FR-073: posts one notification per budget, replacing its earlier alert; nothing without permission. */
        override fun show(alert: BudgetAlert) {
            if (!mayPost()) return
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            createChannel(manager)
            val notification =
                Notification
                    .Builder(context, BUDGET_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_inbox_notification)
                    .setContentTitle(title(alert))
                    .setContentText(text(alert))
                    .setContentIntent(openAppIntent(context))
                    .setAutoCancel(true)
                    .build()
            manager.notify(BUDGET_NOTIFICATION_TAG, alert.budgetId.value.toInt(), notification)
        }

        // FR-073: the permission is a runtime grant on Android 13 and later
        private fun mayPost(): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        private fun createChannel(manager: NotificationManager) {
            val channel =
                NotificationChannel(
                    BUDGET_CHANNEL_ID,
                    context.getString(R.string.budget_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            manager.createNotificationChannel(channel)
        }

        private fun title(alert: BudgetAlert): String {
            val name = alert.categoryName
            return when {
                name == null -> {
                    context.getString(R.string.budget_alert_title_overall)
                }

                alert.threshold >= BudgetRules.EXCEEDED_PERCENT -> {
                    context.getString(R.string.budget_alert_title_exceeded, name)
                }

                else -> {
                    context.getString(R.string.budget_alert_title_warning, name)
                }
            }
        }

        private fun text(alert: BudgetAlert): String =
            context.getString(
                R.string.budget_alert_text,
                rupiahText(alert.spent.rupiah),
                rupiahText(alert.limit.rupiah),
            )
    }
