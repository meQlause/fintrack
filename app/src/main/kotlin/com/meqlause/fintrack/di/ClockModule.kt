package com.meqlause.fintrack.di

import com.meqlause.fintrack.core.common.time.ClockProvider
import com.meqlause.fintrack.core.common.time.SystemClockProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.Clock
import javax.inject.Singleton

/** DOC-03: the device clock behind [ClockProvider]; use cases receive a Clock and never read the time themselves. */
@Module
@InstallIn(SingletonComponent::class)
object ClockModule {
    @Provides
    @Singleton
    fun provideClockProvider(): ClockProvider = SystemClockProvider()

    @Provides
    fun provideClock(clockProvider: ClockProvider): Clock = clockProvider.clock()
}
