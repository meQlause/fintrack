package com.meqlause.fintrack.core.notification

import android.app.Notification
import android.os.Bundle
import android.service.notification.StatusBarNotification
import com.meqlause.fintrack.core.model.RawNotification
import java.time.Instant

// FR-083, DOC-07: group summaries and ongoing notifications are never payments, so they are skipped unread
internal fun StatusBarNotification.isSummaryOrOngoing(): Boolean {
    val flags = notification.flags
    return (flags and Notification.FLAG_GROUP_SUMMARY) != 0 || (flags and Notification.FLAG_ONGOING_EVENT) != 0
}

// FR-083, DOC-07 step 3: the six fields of RawNotification; a missing extra reads as an empty string
internal fun StatusBarNotification.toRawNotification(): RawNotification {
    val extras = notification.extras
    return RawNotification(
        packageName = packageName,
        title = extras.textOf(Notification.EXTRA_TITLE),
        text = extras.textOf(Notification.EXTRA_TEXT),
        bigText = extras.textOf(Notification.EXTRA_BIG_TEXT),
        postedAt = Instant.ofEpochMilli(postTime),
        notificationKey = key,
    )
}

private fun Bundle.textOf(bundleKey: String): String = getCharSequence(bundleKey)?.toString().orEmpty()
