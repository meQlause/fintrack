package com.meqlause.fintrack.core.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.meqlause.fintrack.core.domain.reminder.CardBillReminder
import com.meqlause.fintrack.core.domain.reminder.InvestmentUpdateReminder
import com.meqlause.fintrack.core.domain.reminder.LoanDueReminder
import com.meqlause.fintrack.core.domain.reminder.Reminder
import com.meqlause.fintrack.core.model.LoanDirection
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val REMINDER_CHANNEL_ID = "bill_reminder"
private const val REMINDER_TAG = "reminder"

// BR-045, BR-055: posts one reminder; the id derives from the reminder key, so a repeat replaces it
@Singleton
public class ReminderNotifier
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) {
        /** BR-045: shows the reminder on the bill reminder channel; tapping it opens the app. */
        public fun show(reminder: Reminder) {
            if (!canPostNotifications(context)) return
            val manager = context.getSystemService(NotificationManager::class.java) ?: return
            createChannel(manager)
            val builder =
                Notification
                    .Builder(context, REMINDER_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_inbox_notification)
                    .setContentTitle(title(reminder))
                    .setContentText(text(reminder))
                    .setContentIntent(openAppIntent(context))
                    .setAutoCancel(true)
            manager.notify(REMINDER_TAG, reminder.reminderKey.hashCode(), builder.build())
        }

        private fun createChannel(manager: NotificationManager) {
            val channel =
                NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    context.getString(R.string.reminder_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            manager.createNotificationChannel(channel)
        }

        private fun title(reminder: Reminder): String =
            when (reminder) {
                is CardBillReminder -> context.getString(R.string.reminder_card_title, reminder.walletName)
                is LoanDueReminder -> loanTitle(reminder)
                is InvestmentUpdateReminder -> context.getString(R.string.reminder_invest_title)
            }

        private fun loanTitle(reminder: LoanDueReminder): String =
            when (reminder.direction) {
                LoanDirection.BORROWED -> context.getString(R.string.reminder_loan_borrowed_title, reminder.personName)
                LoanDirection.LENT -> context.getString(R.string.reminder_loan_lent_title, reminder.personName)
            }

        private fun text(reminder: Reminder): String =
            when (reminder) {
                is CardBillReminder -> {
                    context.getString(
                        R.string.reminder_due_text,
                        reminder.daysBefore,
                        rupiahText(reminder.amount.rupiah),
                    )
                }

                is LoanDueReminder -> {
                    context.getString(
                        R.string.reminder_loan_text,
                        reminder.daysBefore,
                        rupiahText(reminder.outstanding.rupiah),
                    )
                }

                is InvestmentUpdateReminder -> {
                    context.getString(R.string.reminder_invest_text)
                }
            }
    }
