package com.meqlause.fintrack.feature.onboarding

import android.content.ComponentName
import android.content.Context
import android.provider.Settings

/** FR-003: reads the system notification-access setting for this app (opening it goes through the disclosure). */
internal object NotificationAccess {
    private const val ENABLED_LISTENERS = "enabled_notification_listeners"
    private const val LISTENER_SEPARATOR = ":"

    /** FR-003: true when a listener of this app is enabled in the system settings. */
    fun isGranted(context: Context): Boolean {
        val enabled = Settings.Secure.getString(context.contentResolver, ENABLED_LISTENERS) ?: return false
        return enabled.split(LISTENER_SEPARATOR).any { entry ->
            ComponentName.unflattenFromString(entry)?.packageName == context.packageName
        }
    }
}
