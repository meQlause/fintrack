package com.meqlause.fintrack.core.domain.detection

/** FR-080, FR-090: whether the system grants this app Notification Access, so the listener receives notifications. */
public interface NotificationAccess {
    /** FR-090: true when the notification listener is enabled for this app in system settings. */
    public fun isGranted(): Boolean
}
