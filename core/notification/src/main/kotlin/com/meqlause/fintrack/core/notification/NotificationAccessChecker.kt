package com.meqlause.fintrack.core.notification

import android.content.Context
import android.provider.Settings
import com.meqlause.fintrack.core.domain.detection.NotificationAccess
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val ENABLED_LISTENERS = "enabled_notification_listeners"

/** FR-080, FR-090: the listener is enabled when this package is in the system's enabled listener list */
public class NotificationAccessChecker
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : NotificationAccess {
        override fun isGranted(): Boolean {
            val enabled =
                Settings.Secure.getString(
                    context.contentResolver,
                    ENABLED_LISTENERS,
                )
            return enabled.orEmpty().split(':').any { it.substringBefore('/') == context.packageName }
        }
    }
