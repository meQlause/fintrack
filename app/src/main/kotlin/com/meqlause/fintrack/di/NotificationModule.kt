package com.meqlause.fintrack.di

import com.meqlause.fintrack.core.domain.detection.NotificationAccess
import com.meqlause.fintrack.core.notification.NotificationAccessChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** FR-080, FR-090: binds the system notification-access check that the Pengaturan diagnostics read. */
@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    abstract fun bindNotificationAccess(checker: NotificationAccessChecker): NotificationAccess
}
