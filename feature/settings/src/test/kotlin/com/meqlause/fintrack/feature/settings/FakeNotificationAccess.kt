package com.meqlause.fintrack.feature.settings

import com.meqlause.fintrack.core.domain.detection.NotificationAccess

// FR-090: a fake access check whose answer the test changes, standing in for the system setting
internal class FakeNotificationAccess(
    var granted: Boolean,
) : NotificationAccess {
    override fun isGranted(): Boolean = granted
}
