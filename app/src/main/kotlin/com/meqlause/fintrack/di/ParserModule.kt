package com.meqlause.fintrack.di

import com.meqlause.fintrack.core.parser.NotificationParserRegistry
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** FR-083, DOC-07: the notification parsers; app-specific parsers are listed here as they are added (T5.3). */
@Module
@InstallIn(SingletonComponent::class)
object ParserModule {
    @Provides
    @Singleton
    fun provideNotificationParserRegistry(): NotificationParserRegistry = NotificationParserRegistry(emptyList())
}
